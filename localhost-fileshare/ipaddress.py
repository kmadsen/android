from netifaces import interfaces, ifaddresses, AF_INET
import socket
import fcntl
import struct

def method1():
    # ni.ifaddresses('eth0')
    # return ni.ifaddresses('eth0')[ni.AF_INET][0]['addr']
    return 'i give up'

def printAllInterfaces():
    for ifaceName in interfaces():
        addresses = [i['addr'] for i in ifaddresses(ifaceName).setdefault(AF_INET, [{'addr':'No IP addr'}] )]
        print('%s: %s' % (ifaceName, ', '.join(addresses)))

def method2():
    ifname = 'eth0'
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    return socket.inet_ntoa(fcntl.ioctl(
        s.fileno(),
        0x8915,  # SIOCGIFADDR
        struct.pack('256s', ifname[:15])
    )[20:24])
