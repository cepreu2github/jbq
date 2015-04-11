#!/bin/bash
find ./ -type f -exec rename 'y/A-Z/a-z/' {} \;
