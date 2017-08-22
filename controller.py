from socket import *
import RPi.GPIO as GPIO
import time

#sends a list of functions to the app
def sendFunctions(ip, port):
    tcp=socket(AF_INET, SOCK_STREAM)
    tcp.connect((ip, 50001))
    data = 'Raspberry 1\n'
    data += 'turn on light|c|0\n'
    data += 'turn on light for 5s|c|5\n'
    data += 'turn off light|c|-1\n'
    data += 'alert other device|v|2\n'
    tcp.send(data)
    tcp.close()

#perform actions on the pi
def action(msg, ip, port):
    if(msg == 'hello'):
        print("send func")
        sendFunctions(ip, port)
    else:
        data = float(msg)
        if(data == 0):
            print("turn on")
            GPIO.output(18, GPIO.HIGH)
        elif(data > 0):
            print("turn on then off")
            GPIO.output(18, GPIO.HIGH)
            time.sleep(data)
            GPIO.output(18, GPIO.LOW)
        else:
            print("turn off")
            GPIO.output(18, GPIO.LOW)

#set up server to wait for commands
def service():
    s=socket(AF_INET, SOCK_DGRAM)
    s.bind(('', 50000))
    
    while True:
        data, (ip, port) = s.recvfrom(1024)
        print(ip + ":" + str(port) + " " + data + "\n")
        action(data, ip, port)

def main():
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
