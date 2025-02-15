package org.huho.aggregate.mongo

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.huho.domain.aggregate.mongo.types.MongoLocalDate
import org.huho.domain.aggregate.mongo.types.MongoLocalDateTime
import org.huho.domain.aggregate.mongo.types.MongoLocalTime
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MongoAggregateRepositoryTest : AbstractMongoIntegrationTest() {
    val datetime = MongoLocalDateTime(Clock.System.now().toLocalDateTime(TimeZone.UTC))
    val date =
        MongoLocalDate(
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.UTC)
                .date,
        )
    val time =
        MongoLocalTime(
            Clock.System
                .now()
                .toLocalDateTime(TimeZone.UTC)
                .time,
        )

    @Test
    fun `it should store and find aggregate`() =
        runBlocking {
            // when
            val aggregateId = TestId()
            val aggregate =
                TestAggregate().apply {
                    create(aggregateId, "test note")
                    changeGeneric(Generic.GenericA(1))
                }

            repository.insert(aggregate, TestAggregate.serializer())

            // then
            val fetchedAggregate = repository.find(aggregateId, TestAggregate::class.java, TestAggregate.serializer())

            assertEquals(aggregate, fetchedAggregate)
        }

    @Test
    fun `it should store new aggregate and record created event`() =
        runBlocking {
            // when
            val aggregate =
                TestAggregate().apply {
                    create(TestId())
                }

            repository.insert(aggregate, TestAggregate.serializer())

            // then
            val recordedEvents = eventProcessor.pullEvents()

            assertEquals(
                listOf(TestAggregate.TestCreated(aggregate.id)),
                recordedEvents,
            )
        }

    @Test
    fun `it should record multiple events`() =
        runBlocking {
            // when
            val testId = TestId()
            val generic = Generic.GenericA(1)
            val note = "new note"

            val aggregate =
                TestAggregate().apply {
                    create(testId)
                    changeGeneric(generic)
                    changeNote(note)
                }

            repository.insert(aggregate, TestAggregate.serializer())

            // then
            val recordedEvents = eventProcessor.pullEvents()

            assertEquals(3, recordedEvents.size)
            assertEquals(
                listOf(
                    TestAggregate.TestCreated(testId),
                    TestAggregate.TestGenericChanged(testId, generic),
                    TestAggregate.TestNoteChanged(testId, note),
                ),
                recordedEvents,
            )
        }

    @Test
    fun `it should record new events on fetched aggregate`() =
        runBlocking {
            // given
            val givenTestId = TestId()
            val givenNote = "test note"
            val givenAggregate =
                TestAggregate().apply {
                    create(givenTestId, givenNote)
                }
            repository.insert(givenAggregate, TestAggregate.serializer())
            eventProcessor.pullEvents() // clean recorded events

            // when
            val newNote = "new note"
            val fetchedAggregate = repository.get(givenTestId, TestAggregate::class.java, TestAggregate.serializer())
            fetchedAggregate.changeNote(newNote)
            repository.save(fetchedAggregate, TestAggregate.serializer())

            // then
            val recordedEvents = eventProcessor.pullEvents()
            assertEquals(
                listOf(
                    TestAggregate.TestNoteChanged(givenTestId, newNote),
                ),
                recordedEvents,
            )
        }
}
