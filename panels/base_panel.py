from PyQt5 import QtWidgets, QtCore, uic

class BasePanel(QtWidgets.QWidget):
    
    def __init__(self, ui_path, main_interface):
        super().__init__()
        uic.loadUi(ui_path, self)
        self.main_interface = main_interface