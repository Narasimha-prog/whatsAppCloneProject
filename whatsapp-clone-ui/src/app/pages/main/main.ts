import {
  AfterViewChecked,
  Component,
  ElementRef,
  NgZone,
  OnDestroy,
  OnInit,
  ViewChild
} from '@angular/core';

import { ChatList } from "../../components/chat-list/chat-list";
import { DatePipe } from '@angular/common';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { FormsModule } from '@angular/forms';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';

import SockJS from 'sockjs-client';
import * as stomp from '@stomp/stompjs';

import { Notification } from './notification';
import { AuthService } from '../../core/services/auth';
import { Router } from '@angular/router';
import { ChatResponse, MessageRequest, MessageResponse } from '../../api/models';
import { ChatsService, MessageService } from '../../api/services';

@Component({
  selector: 'app-main',
  imports: [ChatList, DatePipe, PickerComponent, FormsModule],
  templateUrl: './main.html',
  styleUrl: './main.scss'
})
export class Main implements OnInit, OnDestroy, AfterViewChecked {

  @ViewChild('scrollableDiv') scrollableDiv!: ElementRef<HTMLDivElement>;

  socketClient: any = null;
  private notificationSubscription: any;

  chatMessages: MessageResponse[] = [];
  chats: ChatResponse[] = [];
  selectedChat: ChatResponse = {};

  messageContent = '';
  showEmojis = false;

  constructor(
    private chatService: ChatsService,
    private messageService: MessageService,
    private ngZone: NgZone,
    private authService: AuthService,
    private router: Router
  ) {}

  // =============================
  // LIFECYCLE
  // =============================

  ngOnInit(): void {
    if (!this.authService.getToken()) {
      this.router.navigate(['/login']);
      return;
    }

    this.initWebSocket();
    this.getAllChats();
  }

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  ngOnDestroy(): void {
    if (this.socketClient) {
      this.socketClient.deactivate();
      this.notificationSubscription?.unsubscribe();
    }
  }

  // =============================
  // SEND MESSAGE
  // =============================

  sendMessage() {
    if (!this.messageContent || !this.selectedChat.id) return;

    const messageRequest: MessageRequest = {
      chatId: this.selectedChat.id,
      senderId: this.authService.getUserId(),
      content: this.messageContent,
      type: "TEXT"
    };

    this.messageService.saveMessage({ body: messageRequest })
      .subscribe(() => {

        const message: MessageResponse = {
          senderId: this.authService.getUserId(),
          content: this.messageContent,
          type: 'TEXT',
          createdAt: new Date().toISOString(),
          seenBy: [this.authService.getUserId()!]
        };

        this.chatMessages.push(message);
        this.selectedChat.lastMessage = this.messageContent;

        this.messageContent = '';
        this.showEmojis = false;
      });
  }

  keyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }

  // =============================
  // EMOJI
  // =============================

  onSelectEmojis(event: any) {
    const emoji: EmojiData = event.emoji;
    this.messageContent += emoji.native;
  }

  // =============================
  // MEDIA UPLOAD
  // =============================

  uploadMedia(target: EventTarget | null) {
    const file = this.extractFileFromTarget(target);
    if (!file || !this.selectedChat.id) return;

    const reader = new FileReader();

    reader.onload = () => {

      const base64 = reader.result?.toString().split(',')[1];
      if (!base64) return;

      this.messageService.uploadMedia({
        "chat-id": this.selectedChat.id!,
        body: { file }
      }).subscribe(() => {

        const message: MessageResponse = {
          senderId: this.authService.getUserId(),
          content: 'Attachment',
          type: 'IMAGE',
          media: [base64],
          createdAt: new Date().toISOString(),
          seenBy: [this.authService.getUserId()!]
        };

        this.chatMessages.push(message);
        this.selectedChat.lastMessage = 'Attachment';
      });
    };

    reader.readAsDataURL(file);
  }

  extractFileFromTarget(target: EventTarget | null): File | null {
    const input = target as HTMLInputElement;
    if (!input?.files?.length) return null;
    return input.files[0];
  }

  // =============================
  // CHAT SELECTION
  // =============================

  chatSelected(chat: ChatResponse) {
    this.selectedChat = chat;
    this.getAllChatMessages(chat.id!);
    this.setMessagesToSeen();
    this.selectedChat.unreadCount = 0;
  }

  getAllChatMessages(chatId: string) {
    this.messageService.getMessages({ 'chat-id': chatId })
      .subscribe(messages => this.chatMessages = messages);
  }

  setMessagesToSeen() {
    if (!this.selectedChat.id) return;

    this.messageService.setMessageToSeen({
      'chat-id': this.selectedChat.id
    }).subscribe();
  }

  // =============================
  // WEBSOCKET
  // =============================

  initWebSocket() {

    const userId = this.authService.getUserId();
    const token = this.authService.getToken();
    const subURL = `/users/${userId}/chat`;

    this.socketClient = new stomp.Client({
      webSocketFactory: () => new SockJS('http://localhost:7878/ws'),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      reconnectDelay: 5000,

      onConnect: () => {
        this.notificationSubscription =
          this.socketClient.subscribe(subURL, (msg: any) => {
            const notification: Notification = JSON.parse(msg.body);
            this.handleNotification(notification);
          });
      }
    });

    this.socketClient.activate();
  }

  handleNotification(notification: Notification) {
    if (!notification) return;

    this.ngZone.run(() => {

      if (this.selectedChat?.id === notification.chatId) {

        if (notification.notificationType === 'MESSAGE' ||
            notification.notificationType === 'IMAGE') {

          const message: MessageResponse = {
            id: notification.messageId,
            senderId: notification.senderId,
            content: notification.content,
            type: notification.messageType,
            createdAt: new Date().toISOString(),
            seenBy: notification.seenBy || []
          };

          this.chatMessages.push(message);

          this.selectedChat.lastMessage =
            notification.messageType === 'IMAGE'
              ? 'Attachment'
              : notification.content;
        }

        if (notification.notificationType === 'SEEN') {

          const msg = this.chatMessages.find(
            m => m.id === notification.messageId
          );

          if (msg) {
            msg.seenBy = [...(msg.seenBy || []), notification.userId];
          }
        }

      } else {

        const destChat = this.chats.find(c => c.id === notification.chatId);

        if (destChat) {
          destChat.lastMessage =
            notification.messageType === 'IMAGE'
              ? 'Attachment'
              : notification.content;

          destChat.lastMessageTime = new Date().toISOString();
          destChat.unreadCount = (destChat.unreadCount || 0) + 1;
        }
      }
    });
  }

  // =============================
  // LOAD CHATS
  // =============================

  private getAllChats() {
    this.chatService.getChatsByReceiver()
      .subscribe(res => this.chats = res);
  }

  // =============================
  // HELPERS
  // =============================

  scrollToBottom() {
    if (!this.scrollableDiv) return;
    const div = this.scrollableDiv.nativeElement;
    div.scrollTop = div.scrollHeight;
  }

  isSelfMessage(message: MessageResponse) {
    return message.senderId === this.authService.getUserId();
  }

  logout() {
    this.authService.logout();
  }
}