import { Component, input, InputSignal, output, ViewEncapsulation } from '@angular/core';
import { ChatResponse, UserResponse } from '../../api/models';
import { DatePipe } from '@angular/common';
import { ChatsService, UserService } from '../../api/services';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-chat-list',
  imports: [DatePipe],
  templateUrl: './chat-list.html',
  styleUrl: './chat-list.scss',
  encapsulation: ViewEncapsulation.None
})
export class ChatList {

  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  searchNewContact = false;
  contacts: Array<UserResponse> = [];
  chatSelected = output<ChatResponse>();

  constructor(
    private chatService: ChatsService,
    private userService: UserService,
    private authService: AuthService
  ) {}

  // ✅ SINGLE CHAT CREATION ONLY
  selectContact(contact: UserResponse) {

    const currentUserId = this.authService.getUserId();

    // 🔥 Check existing single chat
    const existingChat = this.chats().find(chat =>
      chat.chatType=='PRIVATE' &&
      chat.participantIds?.includes(currentUserId!) &&
      chat.participantIds?.includes(contact.id!)

    );

    if (existingChat) {
      this.chatSelected.emit(existingChat);
      this.searchNewContact = false;
      return;
    }

    // 🔥 Create new single chat
    this.chatService.createChat({
     'recipient-id': [ contact.id!]
    }).subscribe(
      {
      next: (chatId:string) => {
        const chat: ChatResponse = {
          id: chatId,
          name: contact.firstName + ' ' + contact.lastName,
          participantIds: [contact.id!],
          chatType: "PRIVATE",
        
        };

        this.chats().unshift(chat);
        this.searchNewContact = false;
        this.chatSelected.emit(chat);
      }
    });
  }

  wrapMessage(lastMessage: string | undefined): string {
    if (!lastMessage) return '';
    if (lastMessage.length <= 20) return lastMessage;
    return lastMessage.substring(0, 17) + "...";
  }

  // ✅ Works for both group and single
  chatsClicked(chat: ChatResponse) {
    this.chatSelected.emit(chat);
  }

  searchContact() {
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.contacts = users;
        this.searchNewContact = true;
      }
    });
  }

  trackByChatId(index: number, chat: ChatResponse): string {
    return chat.id ?? index.toString();
  }

  trackByContactId(index: number, contact: UserResponse): string {
    return contact.id ?? index.toString();
  }

}