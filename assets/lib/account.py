class Account:
    pass


class ElectrumSequence:
    """  Privatekey(type,n) = Master_private_key + H(n|S|type)  """

    def __init__(self, mpk, mpk2 = None, mpk3 = None):
        self.mpk = mpk
        self.mpk2 = mpk2
        self.mpk3 = mpk3

    @classmethod
    def mpk_from_seed(klass, seed):
        curve = SECP256k1
        secexp = klass.stretch_key(seed)
        master_private_key = ecdsa.SigningKey.from_secret_exponent( secexp, curve = SECP256k1 )
        master_public_key = master_private_key.get_verifying_key().to_string().encode('hex')
        return master_public_key

    @classmethod
    def stretch_key(self,seed):
        oldseed = seed
        for i in range(100000):
            seed = hashlib.sha256(seed + oldseed).digest()
        return string_to_number( seed )

    def get_sequence(self, sequence, mpk):
        for_change, n = sequence
        return string_to_number( Hash( "%d:%d:"%(n,for_change) + mpk.decode('hex') ) )

    def get_address(self, sequence):
        if not self.mpk2:
            pubkey = self.get_pubkey(sequence)
            address = public_key_to_bc_address( pubkey.decode('hex') )
        elif not self.mpk3:
            pubkey1 = self.get_pubkey(sequence)
            pubkey2 = self.get_pubkey(sequence, mpk = self.mpk2)
            address = Transaction.multisig_script([pubkey1, pubkey2], 2)["address"]
        else:
            pubkey1 = self.get_pubkey(sequence)
            pubkey2 = self.get_pubkey(sequence, mpk = self.mpk2)
            pubkey3 = self.get_pubkey(sequence, mpk = self.mpk3)
            address = Transaction.multisig_script([pubkey1, pubkey2, pubkey3], 2)["address"]
        return address

    def get_pubkey(self, sequence, mpk=None):
        curve = SECP256k1
        if mpk is None: mpk = self.mpk
        z = self.get_sequence(sequence, mpk)
        master_public_key = ecdsa.VerifyingKey.from_string( mpk.decode('hex'), curve = SECP256k1 )
        pubkey_point = master_public_key.pubkey.point + z*curve.generator
        public_key2 = ecdsa.VerifyingKey.from_public_point( pubkey_point, curve = SECP256k1 )
        return '04' + public_key2.to_string().encode('hex')

    def get_private_key_from_stretched_exponent(self, sequence, secexp):
        order = generator_secp256k1.order()
        secexp = ( secexp + self.get_sequence(sequence, self.mpk) ) % order
        pk = number_to_string( secexp, generator_secp256k1.order() )
        compressed = False
        return SecretToASecret( pk, compressed )
        
    def get_private_key(self, sequence, seed):
        secexp = self.stretch_key(seed)
        return self.get_private_key_from_stretched_exponent(sequence, secexp)

    def get_private_keys(self, sequence_list, seed):
        secexp = self.stretch_key(seed)
        return [ self.get_private_key_from_stretched_exponent( sequence, secexp) for sequence in sequence_list]

    def check_seed(self, seed):
        curve = SECP256k1
        secexp = self.stretch_key(seed)
        master_private_key = ecdsa.SigningKey.from_secret_exponent( secexp, curve = SECP256k1 )
        master_public_key = master_private_key.get_verifying_key().to_string().encode('hex')
        if master_public_key != self.mpk:
            print_error('invalid password (mpk)')
            raise BaseException('Invalid password')
        return True

    def get_input_info(self, sequence):
        if not self.mpk2:
            pk_addr = self.get_address(sequence)
            redeemScript = None
        elif not self.mpk3:
            pubkey1 = self.get_pubkey(sequence)
            pubkey2 = self.get_pubkey(sequence,mpk=self.mpk2)
            pk_addr = public_key_to_bc_address( pubkey1.decode('hex') ) # we need to return that address to get the right private key
            redeemScript = Transaction.multisig_script([pubkey1, pubkey2], 2)['redeemScript']
        else:
            pubkey1 = self.get_pubkey(sequence)
            pubkey2 = self.get_pubkey(sequence, mpk=self.mpk2)
            pubkey3 = self.get_pubkey(sequence, mpk=self.mpk3)
            pk_addr = public_key_to_bc_address( pubkey1.decode('hex') ) # we need to return that address to get the right private key
            redeemScript = Transaction.multisig_script([pubkey1, pubkey2, pubkey3], 2)['redeemScript']
        return pk_addr, redeemScript



class BIP32_Account(Account):

    def __init__(self, mpk, mpk2 = None, mpk3 = None):
        self.mpk = mpk
        self.mpk2 = mpk2
        self.mpk3 = mpk3
    
    @classmethod
    def mpk_from_seed(klass, seed):
        master_secret, master_chain, master_public_key, master_public_key_compressed = bip32_init(seed)
        return master_public_key.encode('hex'), master_chain.encode('hex')

    def get_pubkey(self, sequence, mpk = None):
        if not mpk: mpk = self.mpk

        # length must be 2
        for_change, n = sequence

        master_public_key, master_chain = mpk
        K = master_public_key.decode('hex')
        chain = master_chain.decode('hex')
        for i in sequence:
            K, K_compressed, chain = CKD_prime(K, chain, i)
        return K_compressed.encode('hex')

    def get_address(self, sequence):
        if not self.mpk2:
            pubkey = self.get_pubkey(sequence)
            address = public_key_to_bc_address( pubkey.decode('hex') )
        elif not self.mpk3:
            pubkey1 = self.get_pubkey(sequence)
            pubkey2 = self.get_pubkey(sequence, mpk = self.mpk2)
            address = Transaction.multisig_script([pubkey1, pubkey2], 2)["address"]
        else:
            pubkey1 = self.get_pubkey(sequence)
            pubkey2 = self.get_pubkey(sequence, mpk = self.mpk2)
            pubkey3 = self.get_pubkey(sequence, mpk = self.mpk3)
            address = Transaction.multisig_script([pubkey1, pubkey2, pubkey3], 2)["address"]
        return address

    def get_private_key(self, sequence, seed):
        master_secret, master_chain, master_public_key, master_public_key_compressed = bip32_init(seed)
        chain = master_chain
        k = master_secret
        for i in sequence:
            k, chain = CKD(k, chain, i)
        return SecretToASecret(k, True)

    def get_private_keys(self, sequence_list, seed):
        return [ self.get_private_key( sequence, seed) for sequence in sequence_list]

    def check_seed(self, seed):
        master_secret, master_chain, master_public_key, master_public_key_compressed = bip32_init(seed)
        assert self.mpk == (master_public_key.encode('hex'), master_chain.encode('hex'))

    def get_input_info(self, sequence):
        if not self.mpk2:
            pk_addr = self.get_address(sequence)
            redeemScript = None
        elif not self.mpk3:
            pubkey1 = self.get_pubkey(sequence)
            pubkey2 = self.get_pubkey(sequence, mpk=self.mpk2)
            pk_addr = public_key_to_bc_address( pubkey1.decode('hex') ) # we need to return that address to get the right private key
            redeemScript = Transaction.multisig_script([pubkey1, pubkey2], 2)['redeemScript']
        else:
            pubkey1 = self.get_pubkey(sequence)
            pubkey2 = self.get_pubkey(sequence, mpk=self.mpk2)
            pubkey3 = self.get_pubkey(sequence, mpk=self.mpk3)
            pk_addr = public_key_to_bc_address( pubkey1.decode('hex') ) # we need to return that address to get the right private key
            redeemScript = Transaction.multisig_script([pubkey1, pubkey2, pubkey3], 2)['redeemScript']
        return pk_addr, redeemScript
