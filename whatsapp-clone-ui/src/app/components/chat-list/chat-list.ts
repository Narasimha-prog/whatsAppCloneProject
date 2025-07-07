import { Component, input, InputSignal, output, ViewEncapsulation } from '@angular/core';
import { ChatResponse, UserResponse } from '../../services/models';
import {DatePipe} from '@angular/common';
import { ChatsService, UserService } from '../../services/services';
import { KeycloakService } from '../../utils/keycloak/keycloak';

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
    private keycloakService: KeycloakService
  ){
    
  }
 selectContact(contact: UserResponse) {
    this.chatService.createChat({
      'sender-id': this.keycloakService.userId as string,
      'recipient-id': contact.id as string
    }).subscribe({
      next: (res) => {
        const chat: ChatResponse = {
          id: res.reponse,
          name: contact.firstName + ' ' + contact.lastName,
          recipientIsOnline: contact.online,
          lastMessageTime: contact.lastSeen,
          senderId: this.keycloakService.userId,
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

 

}
