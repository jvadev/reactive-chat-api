package com.jvadev.reactivechatapi.service

import com.jvadev.reactivechatapi.mapToViewModel
import com.jvadev.reactivechatapi.repository.MessageRepository
import com.jvadev.reactivechatapi.toDomain
import org.springframework.stereotype.Service

@Service
class PersistentMessageService(
    private val repository: MessageRepository
): MessageService {
    override suspend fun latest(): List<MessageVM> =
        repository.findLatest().mapToViewModel()


    override suspend fun after(messageId: String): List<MessageVM> =
        repository.findLatest(messageId).mapToViewModel()

    override suspend fun post(message: MessageVM) {
        repository.save(message.toDomain())
    }


}