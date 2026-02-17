package com.lnreddy.WhatsAppClone.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  final  private SimpMessagingTemplate  messagingTemplate;


  public void sendNotification(String userId, Notification notification){
      log.info("Sending ws notification to {} with PayLoad {}",userId,notification);

      messagingTemplate.convertAndSendToUser(
              userId,
              "/chat",notification
      );

  }



}
