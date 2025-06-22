// contracts/RewardToken.sol
// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract RewardToken is ERC20 {
    constructor() ERC20("RewardToken", "RWT") {
        _mint(msg.sender, 1000000 * 10 ** decimals());
    }

    function reward(address user, uint256 amount) public {
        _transfer(msg.sender, user, amount);
    }
}
