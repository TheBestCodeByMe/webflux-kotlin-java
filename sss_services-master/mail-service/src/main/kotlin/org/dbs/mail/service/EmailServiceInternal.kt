package org.dbs.mail.service

import org.dbs.safe.some.mail.dto.NotificationDto


interface EmailServiceInternal {
    fun send(notification: NotificationDto)
}
