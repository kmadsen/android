from http.server import BaseHTTPRequestHandler, HTTPServer
import socketserver
import json
import cgi
import os
import urllib
import socket

import filelist
import ipaddress

class Server(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        
    def do_HEAD(self):
        self._set_headers()
        
    # GET sends back a Hello world message
    def do_GET(self):
        self._set_headers()
        
        filename = os.path.relpath(self.path, '/json_files/')
        if (len(filename) > 10):
            filepath = os.path.relpath(self.path, '/')
            filepath = urllib.unquote(filepath).decode('utf8')
            f = open (filepath, "r") 
            data = json.dumps(json.loads(f.read()))
            print("responsedata %s" % data)
            self.wfile.write(data)        
        else:
            self.json_files = filelist.parse_files_dir('json_files')
            self.wfile.write(json.dumps(self.json_files))        

        
    # POST echoes the message adding a JSON field
    def do_POST(self):
        ctype, pdict = cgi.parse_header(self.headers.getheader('content-type'))
        
        # refuse to receive non-json content
        if ctype != 'application/json':
            self.send_response(400)
            self.end_headers()
            return
            
        # read the message and convert it into a python dictionary
        length = int(self.headers.getheader('content-length'))
        message = json.loads(self.rfile.read(length))
        
        # add a property to the object, just to mess with data
        message['received'] = 'ok'
        
        # send the message back
        self._set_headers()
        self.wfile.write(json.dumps(message))
        
def run(server_class=HTTPServer, handler_class=Server, port=8000):
    server_address = ('', port)
    httpd = server_class(server_address, handler_class)

    ipaddress.printAllInterfaces()
    
    # doesn't always work
    # ipaddress = socket.gethostbyname(hostname)
    # print "Server started http://%s:%s/drives" % (ipaddress, port)

    print('Starting httpd on port %d...' % port)
    httpd.serve_forever()
    
if __name__ == "__main__":
    from sys import argv
    
    if len(argv) == 2:
        run(port=int(argv[1]))
    else:
        run()
