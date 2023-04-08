package org.conservationco.asanahire.exception

class SyncNotFoundException(syncId: Long) : RuntimeException("Sync with ID $syncId not found!")
