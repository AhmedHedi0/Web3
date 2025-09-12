const hre = require("hardhat");

async function main() {
  const RewardToken = await hre.ethers.getContractFactory("RewardToken");
  const token = await RewardToken.deploy(); // deploy and wait internally

  console.log("RewardToken deployed to:", token.target); // `token.address` is now `token.target` in ethers v6
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});