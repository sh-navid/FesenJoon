import tkinter as tk
import sys
import os 

class FloatingButton:
    def __init__(self, master):
        self.master = master

        # Make the window stay on top
        master.attributes('-topmost', True)

        # Remove window decorations (title bar, borders)
        master.overrideredirect(True)

        # Make background transparent
        master.attributes('-alpha', 0.0)

        # Load the image
        try:
            self.image = tk.PhotoImage(file=sys.path[0] + "/LogoX.png")
        except tk.TclError as e:
            print(f"Error loading image: {e}. Make sure the file exists and is a valid image.")
            self.image = None

        # Create the button with the image
        if self.image:
            self.button = tk.Button(master, image=self.image, command=self.on_click, borderwidth=0,
                                    highlightthickness=0)
            self.button.pack()
        else:
            # If no image, create a fallback text button
            self.button = tk.Button(master, text="Click on Nabot", command=self.on_click)
            self.button.pack()

        # Allow dragging
        self.button.bind("<ButtonPress-1>", self.start_move)
        self.button.bind("<ButtonRelease-1>", self.stop_move)
        self.button.bind("<B1-Motion>", self.do_move)

        self.x = 0
        self.y = 0

    def on_click(self):
        # Replace print with notify-send
        os.system('notify-send "Nabot" "Hey; Nabot is here"')

    def start_move(self, event):
        self.x = event.x
        self.y = event.y

    def stop_move(self, event):
        self.x = None
        self.y = None

    def do_move(self, event):
        deltax = event.x - self.x
        deltay = event.y - self.y
        x = self.master.winfo_x() + deltax
        y = self.master.winfo_y() + deltay
        self.master.geometry(f"+{x}+{y}")

root = tk.Tk()
floating_button = FloatingButton(root)
root.mainloop()
