package org.huho.libs.aggregate.mongo

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MongoAggregateRepositoryTest : AbstractMongoIntegrationTest() {
    @Test
    fun `find should return aggregate if it exists`() =
        runBlocking {
            // given
            val aggregateId = TestId()
            val aggregate =
                TestAggregate(aggregateId, "test note").apply {
                    changeGeneric(GenericA(1))
                }

            // when
            repository.insert(aggregate, TestAggregate.serializer())

            // then
            val fetchedAggregate = repository.find(aggregateId, TestAggregate::class.java, TestAggregate.serializer())

            assertEquals(aggregate, fetchedAggregate)
        }
}
