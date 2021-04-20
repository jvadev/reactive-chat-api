package com.jvadev.reactivechatapi.controller

import com.jvadev.reactivechatapi.AbstractIT
import com.jvadev.reactivechatapi.prepareForTesting
import com.jvadev.reactivechatapi.repository.ContentType
import com.jvadev.reactivechatapi.repository.Message
import com.jvadev.reactivechatapi.repository.MessageRepository
import com.jvadev.reactivechatapi.service.MessageVM
import com.jvadev.reactivechatapi.service.UserVM
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.WebTestClient
import java.net.URL
import java.time.Instant
import java.time.temporal.ChronoUnit.MILLIS

@AutoConfigureWebTestClient
internal class MessageControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val repository: MessageRepository
) : AbstractIT() {
    init {
        val now = Instant.now()
        lateinit var lastMessageId: String

        beforeEach {
            coroutineScope {
                val secondBefore = now.minusSeconds(1)
                val twoSecondsBefore = now.minusSeconds(2)
                repository.saveAll(
                    listOf(
                        Message(
                            content = "**testMessage**",
                            contentType = ContentType.PLAIN,
                            sent = twoSecondsBefore,
                            username = "test",
                            userAvatarImageLink = "http://test.com/"
                        ),
                        Message(
                            content = "**testMessage1**",
                            contentType = ContentType.MARKDOWN,
                            sent = secondBefore,
                            username = "test1",
                            userAvatarImageLink = "http://test.com/"
                        ),
                        Message(
                            content = "**testMessage2**",
                            contentType = ContentType.MARKDOWN,
                            sent = now,
                            username = "test2",
                            userAvatarImageLink = "http://test.com/",
                        )
                    )
                ).collect()
                lastMessageId = repository.findLatest().first().id ?: ""
            }
        }

        afterEach {
            coroutineScope {
                repository.deleteAll()
            }
        }

        test("should return latest messages") {
            coroutineScope {
                // when:
                webTestClient.get()
                    .uri("/api/v1/messages")
                    .exchange()
                    // then:
                    .expectStatus()
                    .isOk
                    .expectBodyList(MessageVM::class.java)
                    .consumeWith<Nothing> {
                        it.responseBody?.map { messageVM ->
                            messageVM.prepareForTesting()
                        } shouldBe listOf(
                            MessageVM(
                                content = "**testMessage**",
                                user = UserVM(name = "test", avatarImageLink = URL("http://test.com/")),
                                sent = now.minusSeconds(2).truncatedTo(MILLIS)
                            ),
                            MessageVM(
                                content = "<body><p><strong>testMessage1</strong></p></body>",
                                user = UserVM(name = "test1", avatarImageLink = URL("http://test.com/")),
                                sent = now.minusSeconds(1).truncatedTo(MILLIS)
                            ),
                            MessageVM(
                                content = "<body><p><strong>testMessage2</strong></p></body>",
                                user = UserVM(name = "test2", avatarImageLink = URL("http://test.com/")),
                                sent = now.truncatedTo(MILLIS)
                            )
                        )
                    }
            }
        }

        test("should return message list by id") {
            coroutineScope {
                // when:
                webTestClient.get()
                    .uri("/api/v1/messages?lastMessageId=$lastMessageId")
                    .exchange()
                    // then:
                    .expectStatus()
                    .isOk
                    .expectBodyList(MessageVM::class.java)
                    .consumeWith<Nothing> {
                        it.responseBody?.map { messageVM -> messageVM.prepareForTesting() }?.shouldContainAll(
                            listOf(
                                MessageVM(
                                    content = "<body><p><strong>testMessage1</strong></p></body>",
                                    user = UserVM(name = "test1", avatarImageLink = URL("http://test.com/")),
                                    sent = now.minusSeconds(1).truncatedTo(MILLIS)
                                ),
                                MessageVM(
                                    content = "<body><p><strong>testMessage2</strong></p></body>",
                                    user = UserVM(name = "test2", avatarImageLink = URL("http://test.com/")),
                                    sent = now.truncatedTo(MILLIS)
                                )
                            )
                        )
                    }
            }
        }
    }
}