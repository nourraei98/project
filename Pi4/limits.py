import serial
import io 
print("hello")
out = "hello world"

ser = serial.Serial("/dev/ttyACM0", 9600) #opens serial port with this baudrate
ser.timeout=60 
print("h")
print(ser.name)
ser.write(b'Lello world')
#this gives you the name of the port youre using
#ser.open() #returns if port is open 
#ser.baudrate=9600 #sets baudrate in another way
#reader = io.open("limits.txt","rb")
#writer = io.open("Sample1.txt","wb") 
#sio = io.TextIOWrapper(io.BufferedRWPair(ser, ser)) #bufferes streams that are both readable and writeable
print("h")
str1="hello"
while True:
    
    line = ser.readline()
    print(line)
    print("kk")

#sio.write('hello world')#pass in used array
print("h")
#sio.flush() # it is buffering. required to get the data out *now*
#sio.close()
print("h")
#hello = sio.readline() this is to read from the stm so reieving
#print(hello == unicode("hello\n")) #unicode? ascii? up to you to decide the format
print("hello3")

#values = open("limits.txt").read().split()
 
#function that convrts the string commands into bit array
def tobits(s):
    result = []
    for c in s:
        bits = bin(ord(c))[2:]
        bits = '00000000'[len(bits):] + bits
        result.extend([int(b) for b in bits])
    return result
used = tobits(out)
#print(used)

#function to convert the int array into a string to be passed via serialize
def convert(list):
    s = [str(i) for i in list]
    res = "".join(s)
    return(res)

# Driver code
#list = [1, 2, 3]
#print(convert(list))
#print(type(convert(list)))

#if statemenets for commands
#highestX=int(values[0])
#highestY=int(values[1])
#i = 0
#out = ""
#if i < highestX: # moving in x direction, we can use i to track later..
    #create array , convert it into bit array and then send over the pyserial thing to the stem
    #out = out + "L2" #add instructions to string
    #used = tobits(out) #this is the array of bits that has the commands but in bit format
    #converted = convert(used) #this converts array of bits (used) into a string
    #print(converted)
    #print(out)

#sio.write(str(converted)) #pass in used array
#sio.flush() # it is buffering. required to get the data out *now*

#while(True): #keeps stream open for listening
 #   warn = sio.readline() #reads the inputs from stream
  #  if(warn): #if whatever is read from the sream = stop
   #     break
#sio.close()
