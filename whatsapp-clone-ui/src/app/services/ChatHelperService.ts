import { Injectable } from '@angular/core';
import { AuthService } from '../core/services/auth';
import { ChatResponse } from '../../app/api/models';

@Injectable({
  providedIn: 'root'
})
export class ChatHelperService {

  constructor(private authService: AuthService) {}

  async getSenderId(chat: ChatResponse): Promise<string> {

    const userId = this.authService.getUserId();
    
    if (!userId) throw new Error('User not logged in');

    if (chat.chatType === 'GROUP') {
      // For group chat, maybe return logged-in user as sender
      return userId;
    }

    // For private chat, find the other participant
    if (chat.participantIds && chat.participantIds.length === 2) {
      const other = chat.participantIds.find(id => id !== userId);
      if (!other) throw new Error('Other participant not found');
      return other;
    }

    throw new Error('Invalid chat data');
  }
}
