import pkg from 'hardhat';
const { ethers } = pkg;
import { expect } from "chai";

describe("RewardToken", function () {
  // Declare variables to hold the deployed contract instance and signers
  let rewardToken;
  let owner;
  let user;

  beforeEach(async function () {
    [owner, user] = await ethers.getSigners();
    const RewardToken = await ethers.getContractFactory("RewardToken");
    rewardToken = await RewardToken.deploy();
    await rewardToken.waitForDeployment();
  });

  it("Should deploy with correct name and symbol", async function () {
    expect(await rewardToken.name()).to.equal("RewardToken");
    expect(await rewardToken.symbol()).to.equal("RWT");
  });

  it("Should mint initial supply to owner", async function () {
    const totalSupply = await rewardToken.totalSupply();
    expect(await rewardToken.balanceOf(owner.address)).to.equal(totalSupply);
  });

  it("Should allow owner to reward users", async function () {
    const rewardAmount = ethers.parseEther("100");
    await rewardToken.reward(user.address, rewardAmount);
    expect(await rewardToken.balanceOf(user.address)).to.equal(rewardAmount);
  });
});