package com.jvadev.reactivechatapi.controller

import com.jvadev.reactivechatapi.service.MessageService
import com.jvadev.reactivechatapi.service.MessageVM
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/messages")
class MessageController(
    private val service: MessageService
) {
    @GetMapping
    suspend fun latest(
        @RequestParam(
            value = "lastMessageId",
            defaultValue = ""
        ) lastMessageId: String
    ): ResponseEntity<List<MessageVM>> {
        val messages = if (lastMessageId.isNotEmpty()) service.after(lastMessageId) else service.latest()

        return if (messages.isEmpty()) {
            with(ResponseEntity.noContent()) {
                header("lastMessageId", lastMessageId)
                build<List<MessageVM>>()
            }
        } else {
            with(ResponseEntity.ok()) {
                header("lastMessageId", messages.last().id)
                body(messages)
            }
        }
    }

    @PostMapping
    suspend fun post(@RequestBody request: MessageVM) {
        service.post(request)
    }
}