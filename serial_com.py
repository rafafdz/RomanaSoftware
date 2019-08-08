import serial
import time
from config import Config
from PyQt5 import QtCore
from PyQt5.QtCore import pyqtSignal

class CardReader:

    def __init__(self):
        self.card_port = Config().get_conf("CARD_PORT")
        self.port = None

    def initialize_port(self):
        self.port = serial.Serial(self.card_port, baudrate=9600)
        time.sleep(2)
        
    def is_open(self):
        return self.port is not None and self.port.is_open
        
    def is_working(self):
        if not self.port or not self.port.is_open:
            return False
        
        self.port.timeout = 1
        self.clear_buffers()
        self.port.write(b'C') # Check Status Command
        
        try:
            response = self.port.read()
        except serial.SerialTimeoutException as ex:
            return False
        else:
            return response == b"G"
        
        
    def read_card(self):
        if not self.port or not self.port.is_open:
            self.initialize_port()
        
        self.clear_buffers()
        self.port.write(b'L')
        
        self.port.timeout = 1
        if self.port.read() != b'S':
            raise serial.SerialException("Not ready to listen")
        
        self.port.timeout = 20
        return self.port.read(8).decode('utf-8')
        
        
    def clear_buffers(self):
        self.port.reset_input_buffer()
        self.port.reset_output_buffer()
        
class ReaderWorker(QtCore.QThread):
    
    connection_error = pyqtSignal()
    not_detected = pyqtSignal()
    card_detected = pyqtSignal(str)
    
    def __init__(self):
        super().__init__()
        self.reader = CardReader()
    
    def run(self):
        if not self.reader.is_open():
            try:
                self.reader.initialize_port()
            except serial.SerialException:
                self.connection_error.emit()
                return
            
        if not self.reader.is_working():
            self.connection_error.emit()
            
        try:
            card = self.reader.read_card()
        except serial.SerialException as ex:
            self.connection_error.emit()
        else:
            if card:
                self.card_detected.emit(card)
            else:
                self.not_detected.emit()
        
        
if __name__ == "__main__":
    reader = CardReader()
    reader.initialize_port()
    print(reader.is_working())
    print(reader.read_card())