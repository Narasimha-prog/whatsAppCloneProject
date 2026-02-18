import { AfterViewChecked, Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ChatList } from "../../components/chat-list/chat-list";
import { ChatResponse, MessageRequest, MessageResponse } from '../../services/models';
import { ChatsService, MessageService } from '../../services/services';
import { DatePipe } from '@angular/common';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import { FormsModule } from '@angular/forms';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';
import SockJS from 'sockjs-client';
import * as stomp from '@stomp/stompjs';
import { Notification } from './notification';
import { AuthService } from '../../core/services/auth';


@Component({
  selector: 'app-main',
  imports: [ChatList, DatePipe, PickerComponent, FormsModule],
  templateUrl: './main.html',
  styleUrl: './main.scss'
})
export class Main implements OnInit, OnDestroy ,AfterViewChecked{

    @ViewChild('scrollableDiv') scrollableDiv!: ElementRef<HTMLDivElement>;
  socketClient: any = null;
  private notificationSubscription: any;

  wrapMessage(arg0: string | undefined) {
    throw new Error('Method not implemented.');
  }
  sendMessage() {
    if (this.messageContent) {
      const messageRequest: MessageRequest = {
        chatId: this.selectedChat.id,
        senderId: this.getSenderId(),
        receiverId: this.getReceiverId(),
        content: this.messageContent,
        type: "TEXT"

      };


      this.messageService.saveMessage({
        body: messageRequest
      }).subscribe({
        next: () => {
          const message: MessageResponse = {
            senderId: this.getSenderId(),
            receiverId: this.getReceiverId(),
            content: this.messageContent,
            type: 'TEXT',
            state: 'SENT',
            createdAt: new Date().toString(),

          };
          this.selectedChat.lastMessage = this.messageContent;
          this.chatMessages.push(message);
          this.messageContent = '';
          this.showEmojis = false;

        }
      })
    }



  }
  private getSenderId(): string {
    if (this.selectedChat.senderId === this.authService.getUserId()) {
      return this.selectedChat.senderId as string;
    }
    return this.selectedChat.recipientId as string;
  }

  private getReceiverId(): string {
    if (this.selectedChat.senderId === this.authService.getUserId()) {
      return this.selectedChat.recipientId as string;
    }
    return this.selectedChat.senderId as string;
  }
  onClick() {
    this.setMessagesToSeen();

  }
  keyDown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.sendMessage();
    }
  }
  messageContent = '';


  onSelectEmojis(emojiSelected: any) {
    const emoji: EmojiData = emojiSelected.emoji;
    this.messageContent += emoji.native;
  }

  showEmojis = false;


  uploadMedia(target: EventTarget | null) {
  
    const file=this.extractFileFromTarget(target);
    if(file !== null){
      const reader = new FileReader();

      reader.onload=()=>{
            const mediaLines: undefined | string=reader.result?.toString().split(',')[1];
    if (!mediaLines) {
     console.error('Could not extract media content.');
          return;
                }
            this.messageService.uploadMedia({
              "chat-id": this.selectedChat.id as string,
               body: {
                file: file
               }
            }).subscribe({
              next : ()=>{
                const message: MessageResponse= {
                      senderId: this.getSenderId(),
                      receiverId: this.getReceiverId(),
                      content: 'Attachment',
                      type: 'IMAGE',
                      state: 'SENT',
                      media: [mediaLines],
                      createdAt: new Date().toString(),



                }
                
                this.chatMessages.push(message);
              },
               error: (err) => {
               console.error('Upload failed:', err);
        }
            });
      };

      reader.readAsDataURL(file);
    }

  }
  extractFileFromTarget(target: EventTarget | null) :File | null{
   const htmlInputTarget=target as HTMLInputElement;

    if(target === null || htmlInputTarget.files === null) return null;

    return htmlInputTarget.files[0];
  }


  isSelfMessage(message: MessageResponse) {


    return message.senderId === this.authService.getUserId();
  }

  chatMessages: MessageResponse[] = [];
  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse = {};

  constructor(
    private chatService: ChatsService,
    private messageService: MessageService,
    private ngZone: NgZone,
    private authService:AuthService

  ) { }

  ngAfterViewChecked(): void {
   this.scrollToBottom();
  }

 private scrollToBottom() {
    if (this.scrollableDiv) {
      const div = this.scrollableDiv.nativeElement;
      div.scrollTop = div.scrollHeight;
    }
  }


  ngOnDestroy(): void {
    if (this.socketClient !== null) {
      this.socketClient.disconnect();
      this.notificationSubscription.unsubscribe();

      this.socketClient = null;
    }
  }


  chatSelected(chatResponse: ChatResponse) {

    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    this.setMessagesToSeen();
    this.selectedChat.unreadCount = 0;
  }

  setMessagesToSeen() {
    this.messageService.setMessageToSeen({
      'chat-id': this.selectedChat.id as string,

    }).subscribe({
      next: () => { }
    })
  }

  getAllChatMessages(chatId: string) {
    this.messageService.getMessages({
      'chat-id': chatId
    }).subscribe({
      next: (messages) => {
        this.chatMessages = messages
      }
    })


  }

  userProfile() {
    window.location.href = '/profile';
  }


  ngOnInit(): void {
    this.initWebSocket();
    this.getAllChats();

  }
  initWebSocket() {

  const sub = this.authService.getUserId;
  const token = this.authService.getToken;
  const subURL = `/users/${sub}/chat`;

  this.socketClient = new stomp.Client({
    webSocketFactory: () => new SockJS('http://localhost:7878/ws'),
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    debug: (str) => {
      console.log('STOMP debug:', str);
    },
    reconnectDelay: 5000,

    // ✅ CORRECTLY SET onConnect handler
    onConnect: () => {
      console.log('WebSocket connected, subscribing to', subURL);
      this.notificationSubscription = this.socketClient.subscribe(
        subURL,
        (message: any) => {
          const notification: Notification = JSON.parse(message.body);
          this.handleNotification(notification);
        }
      );
    },

    onStompError: (frame) => {
      console.error('STOMP error:', frame.headers['message']);
      console.error('Details:', frame.body);
    }
  });

  this.socketClient.activate(); // ✅ Important: Starts the connection
}
  handleNotification(notification: Notification) {
    if (!notification) return;
    this.ngZone.run(() => {
      if (this.selectedChat && this.selectedChat.id === notification.chatId) {
        switch (notification.notificationType) {
          case 'MESSAGE':

          case 'IMAGE':

            const message: MessageResponse =
            {

              senderId: notification.senderId,
              receiverId: notification.receiverId,
              content: notification.content,
              createdAt: new Date().toString(),
              type: notification.messageType,
            }

            if (notification.messageType === 'IMAGE') {
              this.selectedChat.lastMessage = 'Attachment';
            }
            else {
              this.selectedChat.lastMessage = notification.content;
            }

            this.chatMessages.push(message);
            break;

          case 'SEEN':
            this.chatMessages.forEach(m => m.state = 'SEEN');
            break;


        }

      }
      else {
        const destChat = this.chats.find(c => c.id === notification.chatId);

        if (destChat && notification.notificationType !== 'SEEN') {
          if (notification.notificationType === 'MESSAGE') {
            destChat.lastMessage = notification.content;
          } else if (notification.messageType === 'IMAGE') {
            destChat.lastMessage = 'Attachment';
          }

          destChat.lastMessageTime = new Date().toString();
          console.log('Before From Not SEEN:', destChat.unreadCount);
          destChat.unreadCount = (destChat.unreadCount || 0) + 1;
          console.log('After FROM NOT SEEN:', destChat.unreadCount);
        } else if (notification.notificationType === 'MESSAGE') {

          const newChat: ChatResponse = {
            id: notification.chatId,
            senderId: notification.senderId,
            recipientId: notification.receiverId,
            lastMessage: notification.content,
            name: notification.chatName,

            unreadCount: 1,
            lastMessageTime: new Date().toString(),
          }

          this.chats.unshift(newChat);
        }

      }

    });
  }

  private getAllChats() {
      this.chatService.getChatsByReceiver()
        .subscribe({
          next: (res) => {
            this.chats = res;
          }
        })
    }

  logout() {
      this.authService.logout();
    }
  

}
