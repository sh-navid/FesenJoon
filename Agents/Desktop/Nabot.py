import tkinter as tk
from pynput import keyboard
import sys

class FloatingImage:
    def __init__(self, master):
        self.master = master

        master.overrideredirect(True)
        master.attributes('-alpha', 0.0)
        master.attributes('-topmost', True)

        try:
            self.image = tk.PhotoImage(file=sys.path[0] + "/Back.png")
            master.geometry(f"{self.image.width()}x{self.image.height()}")
        except tk.TclError as e:
            print(f"Error loading image: {e}. Make sure the file exists and is a valid image.")
            self.image = None

        if self.image:
            self.label = tk.Label(master, image=self.image)
            self.label.pack(fill=tk.BOTH, expand=True)

        self.text_label = tk.Label(master, text="Hello, I'm Nabot!", justify="center", borderwidth=2, relief="solid")
        self.text_label.place(relx=0.5, rely=0.8, anchor="center")

        self.center_window()
        self.master.withdraw()

    def center_window(self):
        screen_width = self.master.winfo_screenwidth()
        screen_height = self.master.winfo_screenheight()
        window_width = self.image.width()
        window_height = self.image.height()
        x = (screen_width // 2) - (window_width // 2)
        y = (screen_height // 2) - (window_height // 2)
        self.master.geometry(f"{window_width}x{window_height}+{x}+{y}")

    def toggle_window(self):
        if self.master.winfo_viewable():
            self.master.withdraw()
        else:
            self.master.deiconify()

def on_hotkey_press(key):
    global floating_image
    try:
        if key == keyboard.Key.ctrl_l or key == keyboard.Key.ctrl_r:
            floating_image.toggle_window()
    except AttributeError:
        pass

root = tk.Tk()
floating_image = FloatingImage(root)

listener = keyboard.Listener(on_press=on_hotkey_press)
listener.start()

root.mainloop()
