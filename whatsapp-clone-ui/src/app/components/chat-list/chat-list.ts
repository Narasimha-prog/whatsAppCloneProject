import { Component, input, InputSignal, output, ViewEncapsulation } from '@angular/core';
import { ChatResponse, UserResponse } from '../../services/models';
import {DatePipe} from '@angular/common';
import { ChatsService, UserService } from '../../services/services';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-chat-list',
  imports: [ DatePipe],
  templateUrl: './chat-list.html',
  styleUrl: './chat-list.scss',
    encapsulation: ViewEncapsulation.None
})
export class ChatList {

    chats: InputSignal<ChatResponse[]> =input<ChatResponse[]>([]);
    searchNewContact =false;
    contacts: Array<UserResponse>=[];
    chatSelected=output<ChatResponse>();

  constructor( 
    private chatService: ChatsService,
    private userService: UserService,
     private authService: AuthService
  ){
    
  }
 selectContact(contact: UserResponse) {
  const existingChat = this.chats().find(
    chat => chat.recipientId === contact.id || chat.senderId === contact.id
  );

  if (existingChat) {
    // Chat already exists, just select it
    this.chatSelected.emit(existingChat);
    this.searchNewContact = false;
    return;
  }
    this.chatService.createChat({
      'sender-id': this.authService.getUserId() as string,
      'recipient-id': contact.id as string
    }).subscribe({
      next: (res) => {
        const chat: ChatResponse = {
          id: res.reponse,
          name: contact.firstName + ' ' + contact.lastName,
          recipientIsOnline: contact.online,
          lastMessageTime: contact.lastSeen,
          senderId: this.authService.getUserId(),
          recipientId: contact.id
        };
        this.chats().unshift(chat);
        this.searchNewContact = false;
        this.chatSelected.emit(chat);
      }
    });

  }
wrapMessage(lastmessage: string|undefined): string {

  if(lastmessage && lastmessage.length <=20){
    return lastmessage;
  }
  return lastmessage?.substring(0,17)+"...";
}

chatsClicked(chat: ChatResponse) {
       this.chatSelected.emit(chat);
}
searchContact() {
   this.userService.getAllUsers().subscribe({
    next: (users)=>  {
      this.contacts= users;
      this.searchNewContact=true;
    }
   })
}

trackByChatId(index: number, chat: ChatResponse): string {
  if (chat.id) {
    return chat.id;
  }
  return index.toString(); // fallback for undefined id
}


trackByContactId(index: number, contact: UserResponse): string {
  if (contact.id) {
    return contact.id;
  }
  return index.toString(); // fallback for undefined id
}

}
