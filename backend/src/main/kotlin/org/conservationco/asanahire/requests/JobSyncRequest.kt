package org.conservationco.asanahire.requests

data class JobSyncRequest(
    val jobId: String,
    val status: RequestState = RequestState.IN_PROGRESS,
) {
    fun isComplete() = status == RequestState.COMPLETE
    fun isInProgress() = status == RequestState.IN_PROGRESS
}
