package com.jvadev.reactivechatapi

import com.jvadev.reactivechatapi.repository.Message
import com.jvadev.reactivechatapi.service.MessageVM
import java.time.temporal.ChronoUnit.MILLIS

fun MessageVM.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(MILLIS))

fun Message.prepareForTesting() = copy(id = null, sent = sent.truncatedTo(MILLIS))