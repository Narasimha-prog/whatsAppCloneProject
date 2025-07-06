import { Component, OnInit } from '@angular/core';
import { ChatList } from "../../components/chat-list/chat-list";
import { ChatResponse, MessageRequest, MessageResponse } from '../../services/models';
import { ChatsService, MessageService } from '../../services/services';
import { KeycloakService } from '../../utils/keycloak/keycloak';
import { DatePipe } from '@angular/common';
import { PickerComponent } from '@ctrl/ngx-emoji-mart';
import {FormsModule} from '@angular/forms';
import { EmojiData } from '@ctrl/ngx-emoji-mart/ngx-emoji';


@Component({
  selector: 'app-main',
  imports: [ChatList, DatePipe, PickerComponent,FormsModule],
  templateUrl: './main.html',
  styleUrl: './main.scss'
})
export class Main implements OnInit {
sendMessage() {
  if(this.messageContent){
    const messageRequest:MessageRequest={
      chatId: this.selectedChat.id ,
      senderId: this.getSenderId(),
      receiverId: this.getReceiverId(),
      content: this.messageContent,
      type: "TEXT"
    };


    this.messageService.saveMessage({
      body: messageRequest
    }).subscribe({
      next: ()=>{
        const message:MessageResponse={
          senderId: this.getSenderId(),
          receiverId: this.getReceiverId(),
          content: this.messageContent,
          type: 'TEXT',
          state: 'SENT',
          createdAt: new Date().toString(),

        };
        this.selectedChat.lastMessage=this.messageContent;
        this.chatMessages.push(message);
        this.messageContent='';
        this.showEmojis=false;

      }
    })
  }



}
 private getSenderId() : string{
    if(this.selectedChat.id === this.keycloakService.userId){
      return this.selectedChat.id as string;
    }
    return this.selectedChat.recipientId as string;
  }

private getReceiverId(): string{
  if(this.selectedChat.id === this.keycloakService.userId){
      return this.selectedChat.recipientId as string;
    }
    return this.selectedChat.id as string;
}

onClick() {
  this.setMessagesToSeen();

}
keyDown(event: KeyboardEvent) {
  if(event.key==='Enter'){
    this.sendMessage();
  }
}
messageContent ='';


  onSelectEmojis(emojiSelected: any) {
    const emoji:EmojiData=emojiSelected.emoji;
    this.messageContent += emoji.native;
  }

  showEmojis = false;


  uploadMedia(arg0: EventTarget | null) {
    
  }


  isSelfMessage(message: MessageResponse) {


    return message.senderId === this.keycloakService.userId;
  }

  chatMessages: MessageResponse[] = [];
  chats: Array<ChatResponse> = [];
  selectedChat: ChatResponse = {};

  constructor(
    private keycloakService: KeycloakService,
    private chatService: ChatsService,
    private messageService: MessageService

  ) { }


  chatSelected(chatResponse: ChatResponse) {

    this.selectedChat = chatResponse;
    this.getAllChatMessages(chatResponse.id as string);
    this.setMessagesToSeen();
    this.selectedChat.unreadCount=0;
  }

  setMessagesToSeen() {
    this.messageService.setMessageToSeen({
       'chat-id': this.selectedChat.id as string,

    }).subscribe({
      next:()=>{}
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
    this.keycloakService.accountManagement();
  }


  ngOnInit(): void {
    this.getAllChats();

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
    this.keycloakService.logOut();
  }

}
