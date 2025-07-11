#!/bin/bash

# Check for correct number of arguments
if [ "$#" -ne 3 ]; then
  echo "Usage: $0 <target_directory> <file_to_replace> <replacement_file>"
  exit 1
fi

TARGET_DIR="$1"
FILE_TO_REPLACE="$2"
REPLACEMENT_FILE="$3"

# Check if target directory exists
if [ ! -d "$TARGET_DIR" ]; then
  echo "Error: Target directory '$TARGET_DIR' not found."
  exit 1
fi

# Check if replacement file exists
if [ ! -f "$REPLACEMENT_FILE" ]; then
  echo "Error: Replacement file '$REPLACEMENT_FILE' not found."
  exit 1
fi

echo "Replacing all instances of '$FILE_TO_REPLACE' in '$TARGET_DIR' with '$REPLACEMENT_FILE'..."

# Find and replace files
find "$TARGET_DIR" -type f -name "$FILE_TO_REPLACE" -print0 | while IFS= read -r -d $'\0' file; do
  echo "Replacing: $file"
  cp -f "$REPLACEMENT_FILE" "$file"
done

echo "Replacement complete."