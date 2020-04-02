import os
import json

def parse_files_dir(directory_name):
    list_files = os.listdir(directory_name)

    list_filename = []
    for filename in list_files:
        list_filename.append(filename)

    response = {}
    response[directory_name] = list_filename
    return response
