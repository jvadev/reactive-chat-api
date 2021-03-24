package com.jvadev.reactivechatapi.controller

import com.jvadev.reactivechatapi.service.MessageService
import com.jvadev.reactivechatapi.service.MessageVM
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HtmlController(
    private val service: MessageService
) {

    @GetMapping("/")
    suspend fun index(model: Model): String {
        val messages: List<MessageVM> = service.latest()

        model["messages"] = messages
        model["lastMessageId"] = messages.lastOrNull()?.id ?: ""
        return "chat"
    }
}