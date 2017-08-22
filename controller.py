from socket import *
import RPi.GPIO as GPIO
import time

notify = 0
notify_ip = ""
notify_port = 0
notify_message = ""

def sendUDP(ip, port, msg):
    udp = socket(AF_INET, SOCK_DGRAM)
    udp.sendto(msg, (ip, port))

#sends a list of functions to the app
def sendTCP(ip, port, data):
    tcp=socket(AF_INET, SOCK_STREAM)
    tcp.connect((ip, port))
    tcp.send(data)
    tcp.close()

#perform actions on the pi
def action(msg, ip, port):
    global notify
    global notify_ip
    global notify_port
    global notify_msg
    
    info = msg.split("|")
    if(len(info) == 1):
        d = info[0]
        if(d == 'hello'):
            print("sending device functions\n")
            port = 50001
            data = 'Raspberry 1\n'
            data += 'turn on light|c|0\n'
            data += 'turn on light for 5s|c|5\n'
            data += 'turn off light|c|-1\n'
            data += 'alert other device|v|ip,port,on\n'
            data += 'turn off alert|v|ip,port,off\n'
            sendTCP(ip, port, data)
        else:
            value = float(d)
            if(value == 0):
                print("turning light on\n")
                GPIO.output(18, GPIO.HIGH)
            elif(value > 0):
                print("turning light on for 5 seconds\n")
                GPIO.output(18, GPIO.HIGH)
                time.sleep(value)
                GPIO.output(18, GPIO.LOW)
            else:
                print("turning light off\n")
                GPIO.output(18, GPIO.LOW)
            
            if(notify):
                print("notifying " + notify_ip + ":" + str(notify_port) + " " + notify_msg)
                sendUDP(notify_ip, notify_port, notify_msg)
    else:
        if(info[2] == "on"):
            notify = 1
            notify_ip = info[0]
            notify_port = int(info[1])
            notify_msg = "5"
        else:
            notify = 0

#set up server to wait for commands
def service():
    s=socket(AF_INET, SOCK_DGRAM)
    s.bind(('', 50000))
    
    while True:
        data, (ip, port) = s.recvfrom(1024)
        print(ip + ":" + str(port) + " " + data)
        action(data, ip, port)

def main():
    print("Starting controller...")
    try:
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(18, GPIO.OUT)

        service()
    except Exception as e:
        print(e.message);
    except:
            print("releasing pins")
    finally:
        GPIO.cleanup()

main()
