// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract MyToken is ERC20 {

    constructor() ERC20("jiangyuxuanToken", "JTK") {
        // 初始铸造给部署者
        _mint(msg.sender, 100000 * 10 ** decimals());
    }
}
