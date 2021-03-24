package com.jvadev.reactivechatapi.service

import java.net.URL
import java.time.Instant

data class UserVM(val name: String, val avatarImageLink: URL)

data class MessageVM(val content: String, val user: UserVM, val sent: Instant, val id: String? = null)