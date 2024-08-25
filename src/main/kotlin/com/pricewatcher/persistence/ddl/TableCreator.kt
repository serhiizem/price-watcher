package com.pricewatcher.persistence.ddl

import com.pricewatcher.util.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput

class TableCreator<T>(
    private val tableName: String,
    private val client: DynamoDbEnhancedAsyncClient,
    private val entityClass: Class<T>
) {
    private val log = LoggerFactory.getLogger(this)

    private val provisionedThroughput = ProvisionedThroughput.builder()
        .readCapacityUnits(READ_CAPACITY_UNITS)
        .writeCapacityUnits(WRITE_CAPACITY_UNITS)
        .build()

    fun execute() {
        val table: DynamoDbAsyncTable<out T> =
            client.table(tableName, TableSchema.fromBean(entityClass))
        table.createTable(createEnhancedRequest())
            .thenAccept {
                log.info("Table $tableName created")
            }
            .exceptionally {
                log.error("Error ${it.message}")
                throw it
            }
            .join()
    }

    private fun createEnhancedRequest(): CreateTableEnhancedRequest {
        return CreateTableEnhancedRequest
            .builder()
            .provisionedThroughput(provisionedThroughput)
            .build()
    }

    companion object {
        private const val READ_CAPACITY_UNITS = 1L
        private const val WRITE_CAPACITY_UNITS = 1L
    }
}