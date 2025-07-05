import { Component, OnInit } from '@angular/core';
import { ChatList } from "../../components/chat-list/chat-list";
import { ChatResponse } from '../../services/models';
import { ChatsService } from '../../services/services';

@Component({
  selector: 'app-main',
  imports: [ChatList],
  templateUrl: './main.html',
  styleUrl: './main.scss'
})
export class Main implements OnInit{


  chats: Array<ChatResponse> =[];

  constructor(
    private chatService: ChatsService
  ){}

  ngOnInit(): void {
    this.getAllChats();
    
  }

  private getAllChats(){
    this.chatService.getChatsByReceiver()
         .subscribe({
          next: (res) =>{
            this.chats=res;
          }
         })
  }

}
