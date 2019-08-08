from PyQt5.QtWidgets import QTableWidgetItem, QHeaderView
from base_panel import BasePanel

class ClientInfo(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/client_info.ui', main_interface)
        self.exit_btn.clicked.connect(self.main_interface.change_main)
        header = self.info_table.verticalHeader()
        for i in range(5):
            header.setSectionResizeMode(i, QHeaderView.ResizeToContents)
        
    def set_user_info(self, name, rut, company, card_id, balance):
        data = [name, rut, company, card_id, balance]
        
        for index, value in enumerate(data):
            item = QTableWidgetItem(str(value))
            self.info_table.setItem(index - 1, 1, item)