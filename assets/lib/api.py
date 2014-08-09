import simple_config, wallet, util, interface, wallet, verifier
from commands import known_commands
from wallet import *
from interface import *
from simple_config import *
from util import *
from verifier import *

from decimal import Decimal
import json
import optparse
import os
import re
import ast
import sys
import time
import traceback


from bitcoin import *
import pkgutil
import platform

class create:
    def __init__(self, callback = None):
        config_options = {'wallet_path':"/sdcard/electrum/electrum.dat", 'portable':True, 'verbose':True, 'auto_cycle':True}
        config = SimpleConfig(config_options)
        wallet = Wallet(config)
        self.wallet = wallet
        self.config = config

    def createWallet(self, password = False):
        print "1"
        config = self.config
        wallet = self.wallet
        print "2"
        server = config.get('server')
        if not server: server = pick_random_server()
        w_host, w_port, w_protocol = server.split(':')
        print "3"
        wallet.config.set_key('server', w_host + ':' + w_port + ':' +w_protocol)
        wallet.init_seed(None)
        print "4"
        wallet.save_seed()
        print "5"
        wallet.synchronize() # there is no wallet thread 
        print "6"
            
        if password:
            wallet.update_password(wallet.seed, None, password)
        return "true|" + wallet.seed


class api:

    def __init__(self, callback = None):
        config_options = {'wallet_path':"/sdcard/electrum/electrum.dat", 'portable':True, 'verbose':True, 'auto_cycle':True}
        config = SimpleConfig(config_options)
        wallet = Wallet(config)
        interface = Interface(config)
        interface.register_callback('connected', lambda: sys.stderr.write("Connected to " + interface.connection_msg + "\n"))
        if not interface.start(wait=True):
            print_msg("Not connected, aborting.")
            sys.exit(1)
        wallet.interface = interface
        verifier = WalletVerifier(interface, config)
        verifier.start()
        wallet.set_verifier(verifier)
        synchronizer = WalletSynchronizer(wallet, config)
        synchronizer.start()
        wallet.update()
        self.wallet = wallet
        self.interface = interface
        self._callback = callback
        self.password = None

    def runwallet(self, callback = None):

        return "none"

    def _run(self, method, args, password_getter):
        if method in protected_commands and self.wallet.use_encryption:
            self.password = apply(password_getter,())
        f = eval('self.'+method)
        result = apply(f,args)
        self.password = None
        if self._callback:
            apply(self._callback, ())
        return result
   

    def getaddresshistory(self, addr):
        h = self.wallet.get_history(addr)
        if h is None: h = self.wallet.interface.synchronous_get([ ('blockchain.address.get_history',[addr]) ])[0]
        return h

    def listunspent(self):
        import copy
        l = copy.deepcopy(self.wallet.get_unspent_coins())
        for i in l: i["value"] = str(Decimal(i["value"])/100000000)
        return l

    def createrawtransaction(self, inputs, outputs):
        # convert to own format
        for i in inputs:
            i['tx_hash'] = i['txid']
            i['index'] = i['vout']
        outputs = map(lambda x: (x[0],int(1e8*x[1])), outputs.items())
        tx = Transaction.from_io(inputs, outputs)
        return tx.as_dict()

    def signrawtransaction(self, raw_tx, input_info, private_keys):
        tx = Transaction(raw_tx)
        self.wallet.signrawtransaction(tx, input_info, private_keys, self.password)
        return tx.as_dict()

    def decoderawtransaction(self, raw):
        tx = Transaction(raw)
        return tx.deserialize()

    def sendrawtransaction(self, raw):
        tx = Transaction(raw)
        r, h = self.wallet.sendtx( tx )
        return h

    def createmultisig(self, num, pubkeys):
        assert isinstance(pubkeys, list)
        return Transaction.multisig_script(pubkeys, num)
    
    def freeze(self,addr):
        return self.wallet.freeze(addr)
        
    def unfreeze(self,addr):
        return self.wallet.unfreeze(addr)

    def prioritize(self, addr):
        return self.wallet.prioritize(addr)

    def unprioritize(self, addr):
        return self.wallet.unprioritize(addr)

    def dumpprivkey(self, addr):
        return self.wallet.get_private_key(addr, self.password)

    def dumpprivkeys(self, addresses = None):
        if addresses is None:
            addresses = self.wallet.addresses(True)
        return self.wallet.get_private_keys(addresses, self.password)

    def validateaddress(self,addr):
        isvalid = is_valid(addr)
        out = { 'isvalid':isvalid }
        if isvalid:
            is_mine = self.wallet.is_mine(addr)
            out['address'] = addr
            out['ismine'] = is_mine
            if is_mine:
                out['pubkey'] = self.wallet.get_public_key(addr)
            
        return out

    def getbalance(self, account= None):
        if account is None:
            c, u = self.wallet.get_balance()
        else:
            c, u = self.wallet.get_account_balance(account)

        out = str(Decimal(c)/100000000)
        return out

    def getUnbalance(self, account= None):
        if account is None:
            c, u = self.wallet.get_balance()
        else:
            c, u = self.wallet.get_account_balance(account)


        if u: out = str(Decimal(u)/100000000)
        else: out = str(Decimal(u)/100000000)
        return out

    def getaddressbalance(self, addr):
        c, u = self.wallet.get_addr_balance(addr)
        if u: out = str(Decimal(u)/100000000)
        else: out = str(Decimal(0)/100000000)
        return out


    def getseed(self):
        import mnemonic
        seed = self.wallet.decode_seed(self.password)
        return { "hex":seed, "mnemonic": ' '.join(mnemonic.mn_encode(seed)) }

    def importprivkey(self, sec):
        try:
            addr = self.wallet.import_key(sec,self.password)
            self.wallet.save()
            out = "Keypair imported: ", addr
        except BaseException as e:
            out = "Error: Keypair import failed: " + str(e)
        return out


    def signmessage(self, address, message):
        return self.wallet.sign_message(address, message, self.password)


    def verifymessage(self, address, signature, message):
        return self.wallet.verify_message(address, signature, message)


    def _mktx(self, to_address, amount, fee = None, change_addr = None, domain = None):

        if not is_valid(to_address):
            raise BaseException("Invalid Bitcoin address", to_address)

        if change_addr:
            if not is_valid(change_addr):
                raise BaseException("Invalid Bitcoin address", change_addr)

        if domain is not None:
            for addr in domain:
                if not is_valid(addr):
                    raise BaseException("invalid Bitcoin address", addr)
            
                if not self.wallet.is_mine(addr):
                    raise BaseException("address not in wallet", addr)

        for k, v in self.wallet.labels.items():
            if v == to_address:
                to_address = k
                print_msg("alias", to_address)
                break
            if change_addr and v == change_addr:
                change_addr = k

        amount = int(100000000*amount)
        if fee: fee = int(100000000*fee)
        return self.wallet.mktx( [(to_address, amount)], self.password, fee , change_addr, domain)


    def mktx(self, to_address, amount, fee = None, change_addr = None, domain = None):
        tx = self._mktx(to_address, amount, fee, change_addr, domain)
        return tx.as_dict()


    def payto(self, to_address, amount, fee = None, change_addr = None, domain = None):
        tx = self._mktx(to_address, amount, fee, change_addr, domain)
        r, h = self.wallet.sendtx( tx )
        return h


    def history(self):
        import datetime
        balance = 0
        out = []
        for item in self.wallet.get_tx_history():
            tx_hash, conf, is_mine, value, fee, balance, timestamp = item
            try:
                time_str = datetime.datetime.fromtimestamp( timestamp).isoformat(' ')[:-3]
            except:
                time_str = "----"

            label, is_default_label = self.wallet.get_label(tx_hash)
            if not label: label = tx_hash
            else: label = label + ' '*(64 - len(label) )

            out.append( "%16s"%time_str + "  " + label + "  " + format_satoshis(value)+ "  "+ format_satoshis(balance) )
        return out



    def setlabel(self, tx, label):
        self.wallet.labels[tx] = label
        self.wallet.save()
            

    def contacts(self):
        c = {}
        for addr in self.wallet.addressbook:
            c[addr] = self.wallet.labels.get(addr)
        return c


    def listaddresses(self, show_all = False, show_balance = False, show_label = False):
        out = []
        for addr in self.wallet.addresses(True):
            if show_all or not self.wallet.is_change(addr):
                if show_balance or show_label:
                    item = { 'address': addr }
                    if show_balance:
                        item['balance'] = str(Decimal(self.wallet.get_addr_balance(addr)[0])/100000000)
                    if show_label:
                        label = self.wallet.labels.get(addr,'')
                        if label:
                            item['label'] = label
                else:
                    item = addr
                out.append( item )
        return out

    def Getaddr(self, show_all = False):
        for addr in self.wallet.addresses(True):
            if show_all or not self.wallet.is_change(addr):
                item = addr
                break
        return item
                         
    def help(self, cmd2=None):
        if cmd2 not in known_commands:
            print_msg("\nList of commands:", ', '.join(sorted(known_commands)))
        else:
            _, _, description, syntax, options_syntax = known_commands[cmd2]
            print_msg(description)
            if syntax: print_msg("Syntax: " + syntax)
            if options_syntax: print_msg("options:\n" + options_syntax)
        return None


