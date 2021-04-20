package com.jvadev.reactivechatapi.service

import com.jvadev.reactivechatapi.repository.ContentType
import com.jvadev.reactivechatapi.repository.Message
import com.jvadev.reactivechatapi.repository.MessageRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import java.time.Instant

@DataR2dbcTest
class PersistentMessageServiceTest(
    private val repository: MessageRepository
) : FunSpec({
    val now = Instant.now()

    lateinit var lastMessageId: String

    afterEach {
        coroutineScope {
            repository.deleteAll()
        }
    }

    test("test insertion within coroutine") {
        coroutineScope {
            val twoSecondsBefore = now.minusSeconds(2)
            val secondBefore = now.minusSeconds(1)
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
            repository.findLatest(lastMessageId).first().content shouldBe "**testMessage1**"
        }
    }
})
