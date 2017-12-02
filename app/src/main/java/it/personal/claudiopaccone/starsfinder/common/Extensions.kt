package it.personal.claudiopaccone.starsfinder.common

fun String.getNextUrl() =
        if (this.contains("rel=\"next\"") == false) null
        else this.substringAfter("<").substringBefore(">")