package com.jvadev.reactivechatapi

import com.jvadev.reactivechatapi.repository.ContentType
import com.jvadev.reactivechatapi.repository.Message
import com.jvadev.reactivechatapi.service.MessageVM
import com.jvadev.reactivechatapi.service.UserVM
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.net.URL

fun MessageVM.toDomain(contentType: ContentType = ContentType.MARKDOWN): Message =
    Message(
        content = content,
        contentType = contentType,
        sent = sent,
        username = user.name,
        userAvatarImageLink = user.avatarImageLink.toString(),
        id = id
    )

fun Message.toViewModel(): MessageVM =
    MessageVM(
        content = contentType.render(content),
        user = UserVM(
            name = username,
            avatarImageLink = URL(userAvatarImageLink)
        ),
        sent = sent,
        id = id
    )

fun List<Message>.mapToViewModel(): List<MessageVM> = this.map { it.toViewModel() }

fun ContentType.render(content: String): String = when (this) {
    ContentType.PLAIN -> content
    ContentType.MARKDOWN -> {
        val flavour = CommonMarkFlavourDescriptor()
        HtmlGenerator(content, MarkdownParser(flavour).buildMarkdownTreeFromString(content), flavour).generateHtml()
    }
}

