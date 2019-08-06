from base_panel import BasePanel

class ReaderError(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/reader_error.ui', main_interface)