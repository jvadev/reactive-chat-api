package com.jvadev.reactivechatapi.controller

import com.jvadev.reactivechatapi.AbstractIT
import com.jvadev.reactivechatapi.prepareForTesting
import com.jvadev.reactivechatapi.repository.ContentType
import com.jvadev.reactivechatapi.repository.Message
import com.jvadev.reactivechatapi.repository.MessageRepository
import com.jvadev.reactivechatapi.service.MessageVM
import com.jvadev.reactivechatapi.service.UserVM
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
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

        beforeSpec {
            repository.deleteAll()
        }

        beforeEach {
            coroutineScope {
                val secondBefore = now.minusSeconds(1)
                val twoSecondsBefore = now.minusSeconds(2)
                val messages = repository.saveAll(
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
                )
                messages.collect {
                    System.err.println(it)
                }
            }
        }

        afterEach {
            coroutineScope {
                repository.deleteAll()
            }
        }

        "should return list" {
            coroutineScope {
                repository.findAll().count() shouldBe 3
            }
        }

        "should return normal list" {
            coroutineScope {
                val messages = repository.saveAll(
                        listOf(
                                Message(
                                        content = "**testMessage**",
                                        contentType = ContentType.PLAIN,
                                        sent = now.minusSeconds(2),
                                        username = "test",
                                        userAvatarImageLink = "http://test.com/"
                                ),
                                Message(
                                        content = "**testMessage1**",
                                        contentType = ContentType.MARKDOWN,
                                        sent = now.minusSeconds(1),
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
                )
                messages.count() shouldBe 3
            }
        }

        "should return message list by id" {
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
                            it.responseBody?.map { messageVM -> messageVM.prepareForTesting() } shouldBe
                                    listOf(
                                            MessageVM(
                                                    content = "<body><p><strong>testMessage1</strong></p></body>",
                                                    user = UserVM(name = "test1", avatarImageLink = URL("http://test.com/")),
                                                    sent = now.minusSeconds(1).truncatedTo(MILLIS)
                                            )
                                    )
                        }
            }
        }
    }
    /*private val now = Instant.now()

    private lateinit var lastMessageId: String

    @BeforeEach
    fun setUp() {
        runBlocking {
            val secondBefore = now.minusSeconds(1)
            val twoSecondsBefore = now.minusSeconds(2)
            val messages = repository.saveAll(
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
            )
            lastMessageId = messages.first().id ?: ""
        }
    }*/

    /*@AfterEach
    fun tearDown() {
        runBlocking {
            repository.deleteAll()
        }
    }*/

    /*@Test
    fun shouldReturnMessageById() {
        runBlocking {
            // when:
            webTestClient.get()
                .uri("/api/v1/messages?lastMessageId=$lastMessageId")
                .exchange()
                // then:
                .expectStatus()
                .isOk
                .expectBodyList(MessageVM::class.java)
                .consumeWith<Nothing> {
                    it.responseBody?.map { messageVM -> messageVM.prepareForTesting() } shouldBe
                            listOf(
                                MessageVM(
                                    content = "<body><p><strong>testMessage1</strong></p></body>",
                                    user = UserVM(name = "test1", avatarImageLink = URL("http://test.com/")),
                                    sent = now.minusSeconds(1).truncatedTo(MILLIS)
                                )
                            )
                }
        }
    }*/

    /*@Test
    fun `should return latest messages when message id not specified`() {
        runBlocking {
            // when:
            webTestClient.get()
                .uri("/api/v1/messages")
                .exchange()
                // then:
                .expectStatus()
                .isOk
                .expectBodyList(MessageVM::class.java)
                .consumeWith<Nothing> {
                    it.responseBody?.map { messageVM -> messageVM.prepareForTesting() } shouldBe
                            listOf(
                                MessageVM(
                                    content = "**testMessage**",
                                    user = UserVM(name = "test", avatarImageLink = URL("http://test.com/")),
                                    sent = now.minusSeconds(2).truncatedTo(MILLIS)
                                ),
                                MessageVM(
                                    content = "<body><p><strong>testMessage1</strong></p></body>",
                                    user = UserVM(name = "test1", avatarImageLink = URL("http://test.com/")),
                                    sent = now.minusSeconds(1).truncatedTo(MILLIS)
                                )
                            )
                }
        }
    }*/
}