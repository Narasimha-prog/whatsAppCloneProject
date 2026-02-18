package com.lnreddy.WhatsAppClone.notification.service;

import com.lnreddy.WhatsAppClone.notification.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

  final  private SimpMessagingTemplate  messagingTemplate;


  public void sendNotification(UUID userId, Notification notification){
      log.info("Sending ws notification to {} with PayLoad {}",userId,notification);

      messagingTemplate.convertAndSendToUser(
              userId.toString(),
              "/chat",notification
      );

  }



}
