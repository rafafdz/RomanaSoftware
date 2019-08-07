from base_panel import BasePanel

class ReaderError(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/reader_error.ui', main_interface)
        self.exit_btn.clicked.connect(self.main_interface.clear_and_exit)