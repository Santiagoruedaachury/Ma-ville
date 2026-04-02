package com.maville.service;

import com.maville.model.Notification;
import com.maville.model.Resident;
import com.maville.model.User;
import com.maville.repository.NotificationRepository;
import com.maville.repository.ResidentRepository;
import com.maville.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ResidentRepository residentRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               ResidentRepository residentRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.residentRepository = residentRepository;
    }

    public List<Notification> getByUser(Long userId) {
        return notificationRepository.findByUserIdOrderByIdDesc(userId);
    }

    public List<Notification> getUnread(Long userId) {
        return notificationRepository.findByUserIdAndLuFalse(userId);
    }

    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null && notification.getUser().getId().equals(userId)) {
            notification.setLu(true);
            notificationRepository.save(notification);
        }
    }

    public void notifyUser(Long userId, String message) {
        userRepository.findById(userId).ifPresent(user -> {
            Notification n = new Notification();
            n.setMessage(message);
            n.setLu(false);
            n.setUser(user);
            notificationRepository.save(n);
        });
    }

    public void notifyResidentsByQuartier(String quartier, String message) {
        List<Resident> residents = residentRepository.findAll();
        for (Resident r : residents) {
            if (r.getAdresseResidentielle() != null
                    && r.getAdresseResidentielle().toLowerCase().contains(quartier.toLowerCase())) {
                notifyUser(r.getId(), message);
            }
        }
    }
}
