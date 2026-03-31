package cn.cpoet.jpatcher.util;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;

/**
 * 通知工具
 *
 * @author CPoet
 */
public abstract class NotificationUtil {

    private final static String GROUP_BALLOON = "JPATCHER_NOTIFICATION_BALLOON";

    private NotificationUtil() {
    }

    public static NotificationGroup getBalloonGroup() {
        return NotificationGroupManager.getInstance().getNotificationGroup(GROUP_BALLOON);
    }

    public static Notification initBalloonError(String content) {
        return getBalloonGroup().createNotification(content, NotificationType.ERROR);
    }

    public static Notification initBalloonError(String title, String content) {
        return getBalloonGroup().createNotification(title, content, NotificationType.ERROR);
    }
}
