from tkinter import *
import tkinter as tk
from tkinter import ttk

from cv2 import cv2
from pandas import np
from tensorflow.python.keras.models import model_from_json
from keras.preprocessing import image

def start_webcam():
    # Load the model and the weights
    model = model_from_json(open("model.json", "r").read())
    model.load_weights('model_weight.h5')

    # Load the cascade
    face_cascade = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')

    # To capture video from webcam.
    cap = cv2.VideoCapture(0)

    fps = 0
    number_of_sec = 1

    while True:
        # Read the frame
        is_frame_red_flag, img = cap.read()

        # Convert to grayscale
        gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

        # Detect the faces
        faces = face_cascade.detectMultiScale(gray, 1.1, 4)

        # Draw the rectangle around each face
        for (x, y, w, h) in faces:
            cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)
            roi_gray = gray[y:y + w, x:x + h]  # cropping region of interest i.e. face area from  image
            roi_gray = cv2.resize(roi_gray, (48, 48))
            img_pixels = image.img_to_array(roi_gray)
            img_pixels = np.expand_dims(img_pixels, axis=0)
            img_pixels /= 255

            predictions = model.predict(img_pixels)

            # find max indexed array
            max_index = np.argmax(predictions[0])

            emotions = ('angry', 'fear', 'happy', 'sad', 'surprise', 'neutral')
            predicted_emotion = emotions[max_index]

            cv2.putText(img, predicted_emotion, (int(x), int(y)), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)

        resized_img = cv2.resize(img, (1000, 700))
        cv2.imshow('Emotion detection project ', resized_img)

        if(fps == 60):
            number_of_sec += 1
            fps = 0
            if(number_of_sec > 5):
                break

        # Stop if the X key was pressed
        if cv2.getWindowProperty("Emotion detection project ", cv2.WND_PROP_VISIBLE) < 1:
            break
    fps += 1
    # Release the VideoCapture object
    cap.release()

def save_info():
    print(val1.get())
    print(val2.get())
    print(val3.get())
    print(val4.get())
    print(val5.get())
    print(val6.get())
    print(val7.get())
    print(val8.get())
    print(val9.get())
    print(val10.get())
    print()
    start_webcam()
    # firstname_info = firstname.get()
    # lastname_info = lastname.get()
    # age_info = age.get()
    # age_info = str(age_info)
    # print(firstname_info, lastname_info, age_info)
    #
    # file = open("user.txt", "w")
    # file.write(firstname_info)
    # file.write(lastname_info)
    # file.write(age_info)
    # file.close()
    # print(" User ", firstname_info, " has been registered successfully")
    #
    # firstname_entry.delete(0, END)
    # lastname_entry.delete(0, END)
    # age_entry.delete(0, END)

screen = Tk()
screen.geometry("500x500")
screen.title("Questionaire")

main_frame = Frame(screen)
main_frame.pack(fill=BOTH, expand=1)

my_canvas = Canvas(main_frame)
my_canvas.pack(side=LEFT, fill=BOTH, expand=1)

my_scrollbar = ttk.Scrollbar(main_frame, orient=VERTICAL, command=my_canvas.yview)
my_scrollbar.pack(side=RIGHT, fill=Y)

my_canvas.configure(yscrollcommand=my_scrollbar.set)
my_canvas.bind('<Configure>', lambda e: my_canvas.configure(scrollregion=my_canvas.bbox("all")))

second_frame = Frame(my_canvas)
my_canvas.create_window((0, 0), window=second_frame, anchor="nw")

heading = Label(second_frame, text="Customer satisfaction questionaire", fg="black", anchor="w", font="lucida 19 bold", pady=8, padx=4).pack(fill="both", expand=1)

question1 = Label(second_frame, text="1: Flight schedules are convenient.", padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val1 = tk.IntVar()
r15 = Radiobutton(second_frame, text="Very satisfied", value=5, variable=val1, padx=30, pady=1, anchor="w").pack(fill="both")
r14 = Radiobutton(second_frame, text="Satisfied", value=4, variable=val1, padx=30, pady=1, anchor="w").pack(fill="both")
r13 = Radiobutton(second_frame, text="Neutral", value=3, variable=val1, padx=30, pady=1, anchor="w").pack(fill="both")
r12 = Radiobutton(second_frame, text="Unsatisfied", value=2, variable=val1, padx=30, pady=1, anchor="w").pack(fill="both")
r11 = Radiobutton(second_frame, text="Very unsatisfied", value=1, variable=val1, padx=30, pady=1, anchor="w").pack(fill="both")

question2 = Label(second_frame, text="2: There are suffcient non-stop fights.",padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val2 = tk.IntVar()
r25 = Radiobutton(second_frame, text="Very satisfied", value=5, variable=val2, padx=30, pady=1, anchor="w").pack(fill="both")
r24 = Radiobutton(second_frame, text="Satisfied", value=4, variable=val2, padx=30, pady=1, anchor="w").pack(fill="both")
r23 = Radiobutton(second_frame, text="Neutral", value=3, command = on_select, variable=val2, padx=30, pady=1, anchor="w").pack(fill="both")
r22 = Radiobutton(second_frame, text="Unsatisfied", value=2, command = on_select, variable=val2, padx=30, pady=1, anchor="w").pack(fill="both")
r21 = Radiobutton(second_frame, text="Very unsatisfied", value=1, command = on_select, variable=val2, padx=30, pady=1, anchor="w").pack(fill="both")

question3 = Label(second_frame, text="3: Frequencies of fights are acceptable.",padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val3 = tk.IntVar()
r35 = Radiobutton(second_frame, text="Very satisfied", value=5, command = on_select, variable=val3, padx=30, pady=1, anchor="w").pack(fill="both")
r34 = Radiobutton(second_frame, text="Satisfied", value=4, command = on_select, variable=val3, padx=30, pady=1, anchor="w").pack(fill="both")
r33 = Radiobutton(second_frame, text="Neutral", value=3, command = on_select, variable=val3, padx=30, pady=1, anchor="w").pack(fill="both")
r32 = Radiobutton(second_frame, text="Unsatisfied", value=2, command = on_select, variable=val3, padx=30, pady=1, anchor="w").pack(fill="both")
r31 = Radiobutton(second_frame, text="Very unsatisfied", value=1, command = on_select, variable=val3, padx=30, pady=1, anchor="w").pack(fill="both")

question4 = Label(second_frame, text="4: The amount of fare meets my expectations.",padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val4 = tk.IntVar()
r45 = Radiobutton(second_frame, text="Very satisfied", value=5, command = on_select, variable=val4, padx=30, pady=1, anchor="w").pack(fill="both")
r44 = Radiobutton(second_frame, text="Satisfied", value=4, command = on_select, variable=val4, padx=30, pady=1, anchor="w").pack(fill="both")
r43 = Radiobutton(second_frame, text="Neutral", value=3, command = on_select, variable=val4, padx=30, pady=1, anchor="w").pack(fill="both")
r42 = Radiobutton(second_frame, text="Unsatisfied", value=2, command = on_select, variable=val4, padx=30, pady=1, anchor="w").pack(fill="both")
r41 = Radiobutton(second_frame, text="Very unsatisfied", value=1, command = on_select, variable=val4, padx=30, pady=1, anchor="w").pack(fill="both")

question5 = Label(second_frame, text="5: Delays and cancels are rarely happen.",padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val5 = tk.IntVar()
r55 = Radiobutton(second_frame, text="Very satisfied", value=5, command = on_select, variable=val5, padx=30, pady=1, anchor="w").pack(fill="both")
r54 = Radiobutton(second_frame, text="Satisfied", value=4, command = on_select, variable=val5, padx=30, pady=1, anchor="w").pack(fill="both")
r53 = Radiobutton(second_frame, text="Neutral", value=3, command = on_select, variable=val5, padx=30, pady=1, anchor="w").pack(fill="both")
r52 = Radiobutton(second_frame, text="Unsatisfied", value=2, command = on_select, variable=val5, padx=30, pady=1, anchor="w").pack(fill="both")
r51 = Radiobutton(second_frame, text="Very unsatisfied", value=1, command = on_select, variable=val5, padx=30, pady=1, anchor="w").pack(fill="both")

question6 = Label(second_frame, text="6: Delayed passengers were tolerated fairly.",padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val6 = tk.IntVar()
r65 = Radiobutton(second_frame, text="Very satisfied", value=5, command = on_select, variable=val6, padx=30, pady=1, anchor="w").pack(fill="both")
r64 = Radiobutton(second_frame, text="Satisfied", value=4, command = on_select, variable=val6, padx=30, pady=1, anchor="w").pack(fill="both")
r63 = Radiobutton(second_frame, text="Neutral", value=3, command = on_select, variable=val6, padx=30, pady=1, anchor="w").pack(fill="both")
r62 = Radiobutton(second_frame, text="Unsatisfied", value=2, command = on_select, variable=val6, padx=30, pady=1, anchor="w").pack(fill="both")
r61 = Radiobutton(second_frame, text="Very unsatisfied", value=1, command = on_select, variable=val6, padx=30, pady=1, anchor="w").pack(fill="both")

question7 = Label(second_frame, text="7: Behavior of staff is reliable.",padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val7 = tk.IntVar()
r75 = Radiobutton(second_frame, text="Very satisfied", value=5, command = on_select, variable=val7, padx=30, pady=1, anchor="w").pack(fill="both")
r74 = Radiobutton(second_frame, text="Satisfied", value=4, command = on_select, variable=val7, padx=30, pady=1, anchor="w").pack(fill="both")
r73 = Radiobutton(second_frame, text="Neutral", value=3, command = on_select, variable=val7, padx=30, pady=1, anchor="w").pack(fill="both")
r72 = Radiobutton(second_frame, text="Unsatisfied", value=2, command = on_select, variable=val7, padx=30, pady=1, anchor="w").pack(fill="both")
r71 = Radiobutton(second_frame, text="Very unsatisfied", value=1, command = on_select, variable=val7, padx=30, pady=1, anchor="w").pack(fill="both")

question8 = Label(second_frame, text="8: Staff are polite and friendly.",padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val8 = tk.IntVar()
r85 = Radiobutton(second_frame, text="Very satisfied", value=5, command = on_select, variable=val8, padx=30, pady=1, anchor="w").pack(fill="both")
r84 = Radiobutton(second_frame, text="Satisfied", value=4, command = on_select, variable=val8, padx=30, pady=1, anchor="w").pack(fill="both")
r83 = Radiobutton(second_frame, text="Neutral", value=3, command = on_select, variable=val8, padx=30, pady=1, anchor="w").pack(fill="both")
r82 = Radiobutton(second_frame, text="Unsatisfied", value=2, command = on_select, variable=val8, padx=30, pady=1, anchor="w").pack(fill="both")
r81 = Radiobutton(second_frame, text="Very unsatisfied", value=1, command = on_select, variable=val8, padx=30, pady=1, anchor="w").pack(fill="both")

question9 = Label(second_frame, text="9: Employees's willingness to help is agreeable.",padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val9 = tk.IntVar()
r95 = Radiobutton(second_frame, text="Very satisfied", value=5, command = on_select, variable=val9, padx=30, pady=1, anchor="w").pack(fill="both")
r94 = Radiobutton(second_frame, text="Satisfied", value=4, command = on_select, variable=val9, padx=30, pady=1, anchor="w").pack(fill="both")
r93 = Radiobutton(second_frame, text="Neutral", value=3, command = on_select, variable=val9, padx=30, pady=1, anchor="w").pack(fill="both")
r92 = Radiobutton(second_frame, text="Unsatisfied", value=2, command = on_select, variable=val9, padx=30, pady=1, anchor="w").pack(fill="both")
r91 = Radiobutton(second_frame, text="Very unsatisfied", value=1, command = on_select, variable=val9, padx=30, pady=1, anchor="w").pack(fill="both")

question10 = Label(second_frame, text="10: On board, phone service is sufffcient.",padx=15, pady=8, font=('Helvetica', 9, 'bold'), anchor="w").pack(fill="both")
val10 = tk.IntVar()
r105 = Radiobutton(second_frame, text="Very satisfied", value=5, command = on_select, variable=val10, padx=30, pady=1, anchor="w").pack(fill="both")
r104 = Radiobutton(second_frame, text="Satisfied", value=4, command = on_select, variable=val10, padx=30, pady=1, anchor="w").pack(fill="both")
r103 = Radiobutton(second_frame, text="Neutral", value=3, command = on_select, variable=val10, padx=30, pady=1, anchor="w").pack(fill="both")
r102 = Radiobutton(second_frame, text="Unsatisfied", value=2, command = on_select, variable=val10, padx=30, pady=1, anchor="w").pack(fill="both")
r101 = Radiobutton(second_frame, text="Very unsatisfied", value=1, command = on_select, variable=val10, padx=30, pady=1, anchor="w").pack(fill="both")

register = Button(second_frame, text="Proceed to give us your review through webcam", command=save_info, width=2, height=2, bg="grey", pady=8).pack(fill="both")

screen.mainloop()