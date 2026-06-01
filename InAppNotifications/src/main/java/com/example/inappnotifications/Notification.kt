package com.example.inappnotifications

class Notification (message: String,
                    position: String,
                    start_date: String,
                    end_date: String,
                    status: String
                    ){

    private var message: String = message
    private var position: String  = position
    private var start_date: String = start_date
    private var end_date: String = end_date
    private var status: String = status

    fun getMessage(): String {
        return message
    }

    fun getPosition(): String {
        return position
    }

    fun getStartDate(): String {
        return start_date
    }

    fun getEndDate(): String {
        return end_date
    }

    fun getStatus(): String {
        return status
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun setPosition(position: String) {
        this.position = position
    }

    fun setStartDate(start_date: String) {
        this.start_date = start_date
    }

    fun setEndDate(end_date: String) {
        this.end_date = end_date
    }

    fun setStatus(status: String) {
        this.status = status
    }

    override fun toString(): String {
        return "Notification(message='$message', position='$position', start_date='$start_date', end_date='$end_date', status='$status')"
    }

}