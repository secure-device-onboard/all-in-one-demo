#!/bin/bash

for var in "$@"
do
    printf '%s\0' "$var"
done

printf '\0'