package com.mariogrip.electrumbitcoinwallet.lib;

/**
 * Created by mariogrip on 05.07.14.
 */
public class walletF extends wallet {

    private transaction transaction = new transaction();
    public String addresses() {

       /* String o;
        for a in self.accounts.keys():
        o += self.get_account_addresses(a, include_change)

        if _next:
        for addr in self.next_addresses.values():
        if addr not in o:
        o +=[addr]
        return o
                */
        return null;
    }
/*
    public String get_account_addresses(int a) {

        if (a == null) {
            String o;
            String ac;

            boolean include_change = true;
            o = self.addresses(include_change);
        }else if (a  self.accounts) {
            ac = self.accounts[a]
            o = ac.get_addresses(0)
            if (include_change) {
                o += ac.get_addresses(1);
            }
        }
            return o
        }
    }
    public int[] get_addr_balance(String address) throws IOException, ClassNotFoundException {
        Map h;
        int c;
        int u;
        int[] emty = {0,0};
        //#assert self.is_mine(address)
        h = this.get("addr_history");
        h = (Map) h.get(address);
        if (h.isEmpty()) return emty;
        c = u = 0;
        String[] received_coins;

        for (Entry<String, String> entry : h.entrySet()) {
            String
            String tx = transaction.getTx_hash();
            if  (tx == null) break;
        }

        for i, (addr, value)in enumerate (tx.outputs):
        if addr == address:
        key = tx_hash + ':%d' % i
        received_coins.append(key)

        for tx_hash, tx_height in h:
        tx = self.transactions.get(tx_hash)
        if not tx:continue
                v=0

        for item in tx.inputs:
        addr = item.get('address')
        if addr == address:
        key = item['prevout_hash'] + ':%d' % item['prevout_n']
        value = self.prevout_values.get(key)
        if key in received_coins:
        v -= value

        for i, (addr, value)in enumerate (tx.outputs):
        key = tx_hash + ':%d' % i
        if addr == address:
        v += value

        if tx_height:
        c += v
        else:
        u += v
        return c,u
    }
    */
}
