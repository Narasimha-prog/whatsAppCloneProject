export interface Notification{

    chatId?:string;
    content?: string;
    senderId?: string;
    messageId?: string;
    receiverIds?: Array<string>;
    messageType?: 'TEXT'| 'IMAGE' |  'AUDIO' | 'VIDEO',
    notificationType?: 'SEEN' | 'MESSAGE' | 'IMAGE' | 'VIDEO' | 'AUDIO';
    media?: Array<string>
   
}