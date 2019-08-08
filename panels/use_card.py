from base_panel import BasePanel
from time import time
from PyQt5 import QtCore
from serial_com import ReaderWorker

        
class UseCard(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/use_card.ui', main_interface)
        
        self.reader_worker = ReaderWorker()
        self.reader_worker.not_detected.connect(self._timeout_action)
        self.reader_worker.connection_error.connect(self._error_action)
        self.reader_worker.card_detected.connect(self._card_read_action)
        self.reader_worker.finished.connect(self._set_reader_inactive)
        
        self.reader_active = False
        self.client_card = True
        #self.card_timer.timeout.connect(self._timeout_action)
        self.cancel_btn.clicked.connect(self._cancel_action)
        
    def start_reading(self):
        if not self.reader_active:
            self.reader_worker.start()
            self.reader_active = True
        
    def _timeout_action(self):
        self._set_reader_inactive()
        self.main_interface.change_card_timeout()
        
    def _error_action(self, error_msg):
        self._set_reader_inactive()
        self.main_interface.change_reader_error(error_msg)
        
    def _card_read_action(self, card):
        self._set_reader_inactive()
        self.main_interface.card_read_action(card)
        
    def _cancel_action(self):
        self.main_interface.change_main()
        
    def _set_reader_inactive(self):
        self.reader_active = False
        
    def set_client_card(self):
        self.client_card = True
        self.user_type_lbl.setText("CLIENTE")
        
    def set_admin_card(self):
        self.client_card = False
        self.user_type_lbl.setText("ADMINISTRADOR")