##import io
#ser = serial.Serial("/dev/ttyACM0", 9600)
#ser.baudrate=9600
#sio = io.TextIOWrapper(io.BufferedRWPair(ser, ser))

#sio.write(unicode("hello\n")) #pass in used array
#sio.flush() # it is buffering. required to get the data out *now*
#hello = sio.readline()
#print(hello == unicode("hello\n")) #unicode? ascii? up to you to decide the format


values = open("limits.txt").read().split()

#function that convrts the string commands into bit array
def tobits(s):
    result = []
    for c in s:
        bits = bin(ord(c))[2:]
        bits = '00000000'[len(bits):] + bits
        result.extend([int(b) for b in bits])
    return result

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
highestX=int(values[0])
highestY=int(values[1])
i = 0
out = ""
if i < highestX: # moving in x direction, we can use i to track later..
    #create array , convert it into bit array and then send over the pyserial thing to the stem
    out = out + "L2" #add instructions to string
    used = tobits(out) #this is the array of bits that has the commands but in bit format
    converted = convert(used) #this converts array of bits (used) into a string
    print(converted)
    print(out)


if i < highestY: # moving in y direction
    #stepper_SingleStep(struct stepper step) #move i steps in y direction
        i+=1 #increment
