from base_panel import BasePanel


class ChargeForm(BasePanel):
    def __init__(self, main_interface):
        super().__init__('uis/charge_form.ui', main_interface)