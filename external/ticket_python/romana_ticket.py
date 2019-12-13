#!/usr/bin/python

import locale
import argparse
from abc import ABC, abstractmethod
from datetime import datetime
from escpos.printer import Usb
import escpos.exceptions

locale.setlocale(locale.LC_TIME, 'es_ES.utf-8')
# Adapt to your needs
# Some software barcodes
# p.soft_barcode('code128', 'Hello')
# p.soft_barcode('UPC-A', '123456', module_width=0.18)

def remove_accents(text):
    text = text.replace("á", "a")
    text = text.replace("é", "e")
    text = text.replace("í", "i")
    text = text.replace("ó", "o")
    text = text.replace("ú", "u")
    return text

def int_from_hex(x):
    return int(x[2:], 16)


class AbstractRomanaPrinter(ABC):
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        
    @abstractmethod
    def set(self, *args):
        pass
    
    @abstractmethod
    def separator(self, *args):
        pass
    
    @abstractmethod
    def text(self, *args):
        pass
    
    @abstractmethod
    def textln(self, *args):
        pass

class RomanaPrinter(Usb, AbstractRomanaPrinter):
     
    def __init__(self, vendor_id, product_id, in_ep, out_ep):
        super().__init__(vendor_id, product_id, in_ep=in_ep, out_ep=out_ep)

    def textln(self, text):
        self.text(text + "\n")

    def separator(self, large=False):
        if large:
            font = "a"
            length = 25
        else:
            font = "b"
            length = 38
            
        self.set(font=font, align="center")
        self.textln("-" * length)
        
class TestPrinter(AbstractRomanaPrinter):
    
    def __init__(self, *args, **kwargs):
        super().__init__()
    
    def set(self, *args, **kwargs):
        pass
    
    def text(self, to_print):
        print(to_print, end='')
        
    def textln(self, to_print):
        print(to_print)
        
    def separator(self, *args, **kwargs):
        self.textln("-" * 30)

class BaseTicket:
    
    def __init__(self, service_name, plate, url, total_price):
        self.service_name = service_name
        self.plate = plate
        self.url = url
        self.total_price = total_price

    def print_header(self, printer):
        printer.set(height=2, align="center")
        printer.text("Comprobante Pesaje\n")
        printer.set(text_type="B", align="center")
        printer.text("Romana Autoservicio Maipu\n")
        
    def print_sub_header(self, printer):
        current_date = datetime.now()
        formatted = current_date.strftime("%a %d/%m/%Y %H:%M").capitalize()
        printer.set(align="center")
        printer.textln(remove_accents(formatted))
        
        printer.set(align="center")
        printer.textln("Patente: " + self.plate)
        
        printer.set(align="center")
        printer.text("Servicio: ")
        printer.set(text_type="B", align="center")
        printer.textln(self.service_name)
        
    def print_body(self, printer):
        pass
    
    
    def print_url(self, printer):
        # Dirty Fix. TO DO -> Better aproach:
        if "null" not in self.url:
            printer.set(align="left")
            printer.textln("Puede acceder a esta informacion")
            printer.textln(f"en el link {self.url}")
            
    def print_footer(self, printer):
        printer.set(align="right")
        printer.text("MONTO CARGADO: ")
        printer.set(align="right", width=2)
        printer.textln(f"${self.total_price}")
    
    def print(self, printer, copies):
        for copy in range(copies):
            self.print_header(printer)
            printer.separator()
            self.print_sub_header(printer)
            printer.separator()
            self.print_body(printer)
            printer.separator()
            self.print_url(printer)
            printer.separator()
            self.print_footer(printer)
            
            if copy < copies - 1:
                for _ in range(2): printer.textln("")
                printer.separator(large=True)
                for _ in range(2): printer.textln("")
                
            
class SimpleTicket(BaseTicket):
    
    def __init__(self, plate, url, price, weight):
        super().__init__("SIMPLE", plate, url, price)
        self.weight = weight
        
    def print_body(self, printer):
        printer.set(align="left")
        printer.text("Peso Medido: ")
        printer.set(width=2, text_type="b")
        printer.textln(f"{self.weight} Kg")
    
class TwoPhaseTicketFirst(BaseTicket):
    
    def __init__(self, plate, url, price, first_weight, first_date):
        super().__init__("DOS FASES", plate, url, price)

        self.first_weight = first_weight
        self.first_date = first_date
        
    def print_body(self, printer):
        printer.set(font="a")
        printer.textln(f"Pesaje Inicial:")
        printer.textln(f"- {self.first_weight} Kg  {self.first_date}")
        printer.text("PRIMER PESAJE: ")
        printer.set(align="right", width=2, text_type="b")
        printer.textln(f"{self.first_weight} Kg")
        
        
class TwoPhaseTicketFinal(BaseTicket):
    
    def __init__(self, plate, url, price, first_weight, 
                 first_date, second_weight, second_date):
        super().__init__("DOS FASES", plate, url, price)

        self.first_weight = first_weight
        self.second_weight = second_weight
        self.first_date = first_date
        self.second_date = second_date
        
    def print_body(self, printer):
        printer.set(font="a")
        printer.textln(f"Pesaje Inicial:")
        printer.textln(f"- {self.first_weight} Kg  {self.first_date}")
        printer.textln(f"Pesaje Final:")
        printer.textln(f"- {self.second_weight} Kg  {self.second_date}")
        printer.text("DIFERENCIA: ")
        printer.set(align="right", width=2, text_type="b")
        printer.textln(f"{abs(self.second_weight - self.first_weight)} Kg")
    
class AxisTicket(BaseTicket):
    
    def __init__(self, plate, url, price, *weights):
        super().__init__("EJES", plate, url, price)
        self.weights = weights
        
    def print_body(self, printer):
        printer.set(font="a")
        
        for index, weight in enumerate(self.weights):
            printer.set(align="left", text_type="normal")
            printer.text(f"Eje {index + 1}: ")
            printer.set(align="left", text_type="b")
            printer.textln(f"{weight} Kg")
    
        printer.text("TOTAL: ")
        printer.set(align="right", width=2, text_type="b")
        printer.textln(f"{sum(self.weights)} Kg")

            
def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("vendor_id", type=int_from_hex)
    parser.add_argument("device_id", type=int_from_hex)
    parser.add_argument("in_ep", type=int)
    parser.add_argument("out_ep", type=int)
    parser.add_argument("--check_status", action="store_true")
    parser.add_argument("-wt", "--weight_type", choices=["SIMPLE", "TWO_PHASE_FIRST", "TWO_PHASE_FINAL", "AXIS"])
    parser.add_argument("--plate")
    parser.add_argument("--url")
    parser.add_argument("--price", type=int)
    parser.add_argument("--copies", type=int, nargs='?', const=1)
    parser.add_argument("-sw", "--single_weight", type=int)
    parser.add_argument("-fw", "--first_weight", type=int)
    parser.add_argument("-lw", "--last_weight", type=int)
    parser.add_argument("-fd", "--first_date")
    parser.add_argument("-ld", "--last_date")
    parser.add_argument("-w", "--weight", type=int, action='append', nargs='+')
    
    args = parser.parse_args()
    
    try:
        printer = RomanaPrinter(args.vendor_id, args.device_id, 
                                in_ep=args.in_ep, out_ep=args.out_ep)
    except Exception as ex:
        print("Connection Error, Check USB.", ex)
        exit(1)
    
    if args.check_status:
        try:
            printer.text(" ") # To do: Find a more elegant approach!
        except Exception as ex:
            print("Printing error", ex)
            exit(1)
        else:
            print("Ticket Printer OK")
            exit(0)
    
    common_args = [args.plate.upper(), args.url, args.price]
    
    if args.weight_type == "SIMPLE":
        ticket_class = SimpleTicket
        extra_args = [args.single_weight]
        
    elif args.weight_type == "TWO_PHASE_FIRST":
        ticket_class = TwoPhaseTicketFirst
        extra_args = [args.first_weight, args.first_date]
    
    elif args.weight_type == "TWO_PHASE_FINAL":
        ticket_class = TwoPhaseTicketFinal
        extra_args = [args.first_weight, args.first_date,
                      args.last_weight, args.last_date]
        
    elif args.weight_type == "AXIS":
        ticket_class = AxisTicket
        extra_args = args.weight[0]
    
    all_args = common_args + extra_args
    ticket = ticket_class(*all_args)
    
    try: 
        ticket.print(printer, args.copies)
        
    except Exception as ex:
        print("Could not print ticket", ex)
        exit(1)
        
    else:
        print("Print Ticket Succesful")
        exit(0)
    
if __name__ == "__main__":
    main()
    # printer = TestPrinter(1046, 0x5011, in_ep=1, out_ep=1)
    # ticket = TwoPhaseTicketFinal("BDKL97", "www.romana.tk/JS23FS2", 5000, 3456, "23/11/2019 12:34", 5423, "kek")
    # ticket = TwoPhaseTicket("BDKL97", "www.romana.tk/JS23FS2", 5000, 8149, "23/11/2019 12:34", 1234, "24/11/2019 23:42")
    # ticket = AxisTicket("BDKL97", "www.romana.tk/JS23FS2", 5000, 8149, 1234, 4124, 14245)
    # ticket.print(printer, 2)
